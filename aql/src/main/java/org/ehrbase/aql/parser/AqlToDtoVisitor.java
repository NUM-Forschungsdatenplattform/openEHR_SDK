/*
 * Copyright (c) 2020 vitasystems GmbH and Hannover Medical School.
 *
 * This file is part of project openEHR_SDK
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehrbase.aql.parser;

import com.nedap.archie.datetime.DateTimeParsers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.lang3.StringUtils;
import org.ehrbase.aql.dto.AqlDto;
import org.ehrbase.aql.dto.LogicalOperatorDto;
import org.ehrbase.aql.dto.condition.ComparisonOperatorCondition;
import org.ehrbase.aql.dto.condition.ComparisonOperatorSymbol;
import org.ehrbase.aql.dto.condition.ExistsCondition;
import org.ehrbase.aql.dto.condition.LikeCondition;
import org.ehrbase.aql.dto.condition.LogicalOperatorCondition;
import org.ehrbase.aql.dto.condition.LogicalOperatorCondition.ConditionLogicalOperatorSymbol;
import org.ehrbase.aql.dto.condition.MatchesCondition;
import org.ehrbase.aql.dto.condition.NotCondition;
import org.ehrbase.aql.dto.condition.WhereCondition;
import org.ehrbase.aql.dto.containment.AbstractContainmentExpression;
import org.ehrbase.aql.dto.containment.Containment;
import org.ehrbase.aql.dto.containment.ContainmentClassExpression;
import org.ehrbase.aql.dto.containment.ContainmentNotOperator;
import org.ehrbase.aql.dto.containment.ContainmentSetOperator;
import org.ehrbase.aql.dto.containment.ContainmentSetOperatorSymbol;
import org.ehrbase.aql.dto.containment.ContainmentVersionExpression;
import org.ehrbase.aql.dto.operand.AQLFunction;
import org.ehrbase.aql.dto.operand.AggregateFunction;
import org.ehrbase.aql.dto.operand.BooleanPrimitive;
import org.ehrbase.aql.dto.operand.ColumnExpression;
import org.ehrbase.aql.dto.operand.ComparisonLeftOperator;
import org.ehrbase.aql.dto.operand.DoublePrimitive;
import org.ehrbase.aql.dto.operand.IdentifiedPath;
import org.ehrbase.aql.dto.operand.LikeOperand;
import org.ehrbase.aql.dto.operand.LongPrimitive;
import org.ehrbase.aql.dto.operand.MatchesOperand;
import org.ehrbase.aql.dto.operand.Primitive;
import org.ehrbase.aql.dto.operand.QueryParameter;
import org.ehrbase.aql.dto.operand.SingleRowFunction;
import org.ehrbase.aql.dto.operand.StringPrimitive;
import org.ehrbase.aql.dto.operand.TemporalPrimitive;
import org.ehrbase.aql.dto.operand.Terminal;
import org.ehrbase.aql.dto.operand.TerminologyFunction;
import org.ehrbase.aql.dto.orderby.OrderByExpression;
import org.ehrbase.aql.dto.orderby.OrderByExpression.OrderByDirection;
import org.ehrbase.aql.dto.path.AqlPath;
import org.ehrbase.aql.dto.path.predicate.Predicate;
import org.ehrbase.aql.dto.path.predicate.PredicateHelper;
import org.ehrbase.aql.dto.select.SelectClause;
import org.ehrbase.aql.dto.select.SelectExpression;
import org.ehrbase.util.exception.SdkException;

public class AqlToDtoVisitor extends AqlParserBaseVisitor<Object> {

    private final Map<String, AbstractContainmentExpression> identifierMap = new HashMap<>();
    private final MultiValuedMap<String, IdentifiedPath> selectFieldDtoMultiMap = new ArrayListValuedHashMap<>();

    private List<String> errors;

    @Override
    public AqlDto visitSelectQuery(AqlParser.SelectQueryContext ctx) {

        errors = new ArrayList<>();
        AqlDto aqlDto = new AqlDto();

        // select
        aqlDto.setSelect(visitSelectClause(ctx.selectClause()));
        // from
        aqlDto.setFrom(visitFromClause(ctx.fromClause()));
        // where
        if (ctx.whereClause() != null) {
            aqlDto.setWhere(visitWhereClause(ctx.whereClause()));
        }
        // oder by
        if (ctx.orderByClause() != null) {
            aqlDto.setOrderBy(visitOrderByClause(ctx.orderByClause()));
        }

        if (ctx.limitClause() != null) {

            AqlParser.LimitClauseContext limitClauseContext = ctx.limitClause();

            aqlDto.setLimit(Integer.parseInt(limitClauseContext.limit.getText()));

            if (limitClauseContext.offset != null) {
                aqlDto.setOffset(Integer.parseInt(limitClauseContext.offset.getText()));
            }
        }

        selectFieldDtoMultiMap.entries().forEach(e -> {
            if (identifierMap.containsKey(e.getKey())) {
                e.getValue().setFrom(identifierMap.get(e.getKey()));
            }
        });

        return aqlDto;
    }

    @Override
    public SelectClause visitSelectClause(AqlParser.SelectClauseContext ctx) {

        SelectClause selectClause = new SelectClause();

        selectClause.setDistinct(ctx.DISTINCT() != null);

        if (ctx.top() != null) {

            errors.add("top not yet implemented");
        }

        selectClause.setStatement(
                ctx.selectExpr().stream().map(this::visitSelectExpr).collect(Collectors.toList()));

        return selectClause;
    }

    @Override
    public SelectExpression visitSelectExpr(AqlParser.SelectExprContext ctx) {

        SelectExpression selectExpression = new SelectExpression();

        selectExpression.setColumnExpression(visitColumnExpr(ctx.columnExpr()));

        if (ctx.IDENTIFIER() != null) {
            selectExpression.setAlias(ctx.IDENTIFIER().getText());
        }

        return selectExpression;
    }

    @Override
    public ColumnExpression visitColumnExpr(AqlParser.ColumnExprContext ctx) {

        if (ctx.identifiedPath() != null) {
            return visitIdentifiedPath(ctx.identifiedPath());

        } else if (ctx.functionCall() != null) {
            return visitFunctionCall(ctx.functionCall());
        } else if (ctx.aggregateFunctionCall() != null) {

            return visitAggregateFunctionCall(ctx.aggregateFunctionCall());
        } else if (ctx.primitive() != null) {

            return visitPrimitive(ctx.primitive());
        } else {

            throw new AqlParseException("Invalid ColumnExpr");
        }
    }

    @Override
    public AggregateFunction visitAggregateFunctionCall(AqlParser.AggregateFunctionCallContext ctx) {

        AggregateFunction dto = new AggregateFunction();

        final AQLFunction aqlFunction = findFunctionName(ctx.name);

        dto.setFunctionName(aqlFunction);
        dto.setIdentifiedPath(visitIdentifiedPath(ctx.identifiedPath()));

        return dto;
    }

    private static AQLFunction findFunctionName(Token name) {
        final AQLFunction aqlFunction;
        try {
            aqlFunction = AQLFunction.valueOf(name.getText().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new SdkException(String.format("Unknown function %s ", name.getText()));
        }
        return aqlFunction;
    }

    @Override
    public Primitive visitPrimitive(AqlParser.PrimitiveContext ctx) {

        final Primitive selectPrimitiveDto;

        if (ctx.BOOLEAN() != null) {
            selectPrimitiveDto = new BooleanPrimitive(Boolean.parseBoolean(ctx.getText()));
        } else if (ctx.DATE() != null) {
            String unwrap = unwrapText(ctx);
            selectPrimitiveDto = new TemporalPrimitive(DateTimeParsers.parseTimeValue(unwrap));
        } else if (ctx.numericPrimitive() != null) {
            AqlParser.NumericPrimitiveContext numericPrimitiveContext = ctx.numericPrimitive();
            selectPrimitiveDto = visitNumericPrimitive(numericPrimitiveContext);
        } else if (ctx.STRING() != null) {
            selectPrimitiveDto = new StringPrimitive(unwrapText(ctx));
        } else {
            throw new AqlParseException("Can not handle value " + ctx.getText());
        }

        return selectPrimitiveDto;
    }

    @Override
    public Primitive visitNumericPrimitive(AqlParser.NumericPrimitiveContext ctx) {

        Primitive value;

        if (ctx.REAL() != null) {
            value = new DoublePrimitive(Double.valueOf(ctx.getText()));
        } else if (ctx.INTEGER() != null) {
            value = new LongPrimitive(Long.valueOf(ctx.getText()));
        } else {
            throw new AqlParseException("Can not handle value " + ctx.getText());
        }

        return value;
    }

    @Override
    public IdentifiedPath visitIdentifiedPath(AqlParser.IdentifiedPathContext ctx) {

        IdentifiedPath selectFieldDto = new IdentifiedPath();

        selectFieldDtoMultiMap.put(ctx.IDENTIFIER().getText(), selectFieldDto);
        selectFieldDto.setPath(AqlPath.parse(
                StringUtils.removeStart(getFullText(ctx), ctx.IDENTIFIER().getText())));

        return selectFieldDto;
    }

    @Override
    public SingleRowFunction visitFunctionCall(AqlParser.FunctionCallContext ctx) {

        SingleRowFunction dto = new SingleRowFunction();
        dto.setFunctionName(findFunctionName(ctx.name));

        dto.setOperantList(ctx.terminal().stream().map(this::visitTerminal).toList());

        return dto;
    }

    @Override
    public Terminal visitTerminal(AqlParser.TerminalContext ctx) {

        if (ctx.identifiedPath() != null) {
            return visitIdentifiedPath(ctx.identifiedPath());
        }
        if (ctx.functionCall() != null) {
            return visitFunctionCall(ctx.functionCall());
        }
        if (ctx.PARAMETER() != null) {
            return createParameter(ctx.PARAMETER());
        }
        if (ctx.primitive() != null) {
            return visitPrimitive(ctx.primitive());
        }
        throw new UnsupportedOperationException("Can not parse %s".formatted(ctx.getText()));
    }

    private QueryParameter createParameter(TerminalNode node) {

        QueryParameter queryParameter = new QueryParameter();
        queryParameter.setName(StringUtils.removeStart(node.getText(), "$"));
        return queryParameter;
    }

    @Override
    public Containment visitFromClause(AqlParser.FromClauseContext ctx) {

        return visitFromExpr(ctx.fromExpr());
    }

    @Override
    public Containment visitFromExpr(AqlParser.FromExprContext ctx) {

        return visitContainsExpr(ctx.containsExpr());
    }

    @Override
    public Containment visitContainsExpr(AqlParser.ContainsExprContext ctx) {

        ContainmentSetOperatorSymbol symbol = extractSymbol(ctx);

        if (symbol != null) {

            AqlParser.ContainsExprContext current = ctx;

            List<Object> boolList = new ArrayList<>();

            while (current != null) {

                if (current.containsExpr().size() == 2) {
                    boolList.add(visitContainsExpr(current.containsExpr(0)));
                    Optional.ofNullable(extractSymbol(current)).ifPresent(boolList::add);
                    current = current.containsExpr(1);
                } else {
                    boolList.add(visitContainsExpr(current));
                    current = null;
                }
            }
            return buildContainmentLogicalOperator(boolList);

        } else if (ctx.SYM_LEFT_PAREN() != null) {

            return visitContainsExpr(ctx.containsExpr(0));
        } else {

            AqlParser.ClassExprOperandContext classExprOperandContext = ctx.classExprOperand();

            final AbstractContainmentExpression containmentDto;

            if (classExprOperandContext instanceof AqlParser.ClassExpressionContext) {

                containmentDto = visitClassExpression((AqlParser.ClassExpressionContext) classExprOperandContext);
            } else {

                containmentDto = visitVersionClassExpr((AqlParser.VersionClassExprContext) classExprOperandContext);
            }
            if (ctx.CONTAINS() != null) {

                Containment contains = visitContainsExpr(ctx.containsExpr(0));
                if (ctx.NOT() != null) {

                    ContainmentNotOperator not = new ContainmentNotOperator();
                    not.setContainmentExpression(contains);
                    containmentDto.setContains(not);
                } else {
                    containmentDto.setContains(contains);
                }
            }
            return containmentDto;
        }
    }

    @Override
    public ContainmentClassExpression visitClassExpression(AqlParser.ClassExpressionContext ctx) {

        ContainmentClassExpression containmentDto = new ContainmentClassExpression();
        containmentDto.setType(ctx.IDENTIFIER(0).getText());

        if (ctx.IDENTIFIER().size() == 2) {
            containmentDto.setIdentifier(ctx.IDENTIFIER(1).getText());
            identifierMap.put(containmentDto.getIdentifier(), containmentDto);
        }

        if (ctx.pathPredicate() != null) {
            Predicate predicate = PredicateHelper.buildPredicate(
                    StringUtils.removeEnd(StringUtils.removeStart(getFullText(ctx.pathPredicate()), "["), "]"));

            containmentDto.setPredicates(predicate);
        }

        return containmentDto;
    }

    @Override
    public ContainmentVersionExpression visitVersionClassExpr(AqlParser.VersionClassExprContext ctx) {

        errors.add("version not yet implemented");
        return new ContainmentVersionExpression() {};
    }

    public ContainmentSetOperator buildContainmentLogicalOperator(List<Object> boolList) {

        return (ContainmentSetOperator) buildLogicalOperator(
                boolList,
                s -> {
                    ContainmentSetOperator conditionLogicalOperatorDto = new ContainmentSetOperator();
                    conditionLogicalOperatorDto.setSymbol(s);
                    conditionLogicalOperatorDto.setValues(new ArrayList<>());

                    return conditionLogicalOperatorDto;
                },
                ContainmentSetOperatorSymbol::getPrecedence);
    }

    public LogicalOperatorCondition buildConditionLogicalOperatorDto(List<Object> boolList) {

        return (LogicalOperatorCondition) buildLogicalOperator(
                boolList,
                s -> {
                    LogicalOperatorCondition logicalOperatorCondition = new LogicalOperatorCondition();
                    logicalOperatorCondition.setSymbol(s);
                    logicalOperatorCondition.setValues(new ArrayList<>());

                    return logicalOperatorCondition;
                },
                ConditionLogicalOperatorSymbol::getPrecedence);
    }

    @Override
    public WhereCondition visitWhereClause(AqlParser.WhereClauseContext ctx) {

        return visitWhereExpr(ctx.whereExpr());
    }

    @Override
    public WhereCondition visitWhereExpr(AqlParser.WhereExprContext ctx) {

        if (ctx.identifiedExpr() != null) {
            return visitIdentifiedExpr(ctx.identifiedExpr());
        } else if (ctx.NOT() != null) {

            NotCondition notCondition = new NotCondition();
            notCondition.setConditionDto(visitWhereExpr(ctx.whereExpr(0)));
            return notCondition;
        } else if (ctx.SYM_RIGHT_PAREN() != null) {
            return visitWhereExpr(ctx.whereExpr(0));
        }
        // AND /  OR
        else {
            List<Object> boollist = new ArrayList<>();
            var current = ctx;
            ConditionLogicalOperatorSymbol symbol = extractSymbol(current);

            while (current != null) {
                if (symbol != null) {
                    boollist.add(0, visitWhereExpr(current.whereExpr(1)));
                    boollist.add(0, symbol);
                    current = current.whereExpr(0);
                    symbol = extractSymbol(current);
                } else {
                    boollist.add(0, visitWhereExpr(current));
                    current = null;
                }
            }

            return buildConditionLogicalOperatorDto(boollist);
        }
    }

    @Override
    public WhereCondition visitIdentifiedExpr(AqlParser.IdentifiedExprContext ctx) {

        if (ctx.COMPARISON_OPERATOR() != null) {

            ComparisonOperatorCondition comparisonOperatorCondition = new ComparisonOperatorCondition();
            comparisonOperatorCondition.setSymbol(extractSymbol(ctx.COMPARISON_OPERATOR()));
            comparisonOperatorCondition.setValue(visitTerminal(ctx.terminal()));
            ComparisonLeftOperator statement;

            if (ctx.functionCall() != null) {
                statement = visitFunctionCall(ctx.functionCall());
            } else {
                statement = visitIdentifiedPath(ctx.identifiedPath());
            }
            comparisonOperatorCondition.setStatement(statement);
            comparisonOperatorCondition.setValue(visitTerminal(ctx.terminal()));
            return comparisonOperatorCondition;
        } else if (ctx.likeOperand() != null) {

            LikeCondition likeCondition = new LikeCondition();
            likeCondition.setStatement(visitIdentifiedPath(ctx.identifiedPath()));
            likeCondition.setValue(visitLikeOperand(ctx.likeOperand()));
            return likeCondition;
        } else if (ctx.SYM_LEFT_PAREN() != null) {

            return visitIdentifiedExpr(ctx.identifiedExpr());
        } else if (ctx.EXISTS() != null) {

            ExistsCondition existsCondition = new ExistsCondition();
            existsCondition.setValue(visitIdentifiedPath(ctx.identifiedPath()));
            return existsCondition;
        } else if (ctx.MATCHES() != null) {

            MatchesCondition matchesCondition = new MatchesCondition();
            matchesCondition.setStatement(visitIdentifiedPath(ctx.identifiedPath()));
            matchesCondition.setValues(visitMatchesOperand(ctx.matchesOperand()));
            return matchesCondition;
        } else {

            errors.add("Can not handle %s".formatted(ctx.getText()));
            return null;
        }
    }

    @Override
    public List<MatchesOperand> visitMatchesOperand(AqlParser.MatchesOperandContext ctx) {

        if (CollectionUtils.isNotEmpty(ctx.valueListItem())) {
            return ctx.valueListItem().stream().map(this::visitValueListItem).toList();
        }

        return Collections.emptyList();
    }

    @Override
    public MatchesOperand visitValueListItem(AqlParser.ValueListItemContext ctx) {

        if (ctx.primitive() != null) {
            return visitPrimitive(ctx.primitive());
        } else if (ctx.PARAMETER() != null) {
            return createParameter(ctx.PARAMETER());
        } else {
            errors.add("Terminology not yet implemented");
            return new TerminologyFunction();
        }
    }

    @Override
    public LikeOperand visitLikeOperand(AqlParser.LikeOperandContext ctx) {

        if (ctx.PARAMETER() != null) {
            return createParameter(ctx.PARAMETER());
        } else {
            return new StringPrimitive(unwrapText(ctx));
        }
    }

    @Override
    public List<OrderByExpression> visitOrderByClause(AqlParser.OrderByClauseContext ctx) {

        return ctx.orderByExpr().stream().map(this::visitOrderByExpr).toList();
    }

    public List<String> getErrors() {
        return errors;
    }

    public static String getFullText(ParserRuleContext context) {

        if (context.start == null
                || context.stop == null
                || context.start.getStartIndex() < 0
                || context.stop.getStopIndex() < 0) return context.getText(); // Fallback

        return context.start
                .getInputStream()
                .getText(Interval.of(context.start.getStartIndex(), context.stop.getStopIndex()));
    }

    private static <S, T> LogicalOperatorDto<S, T> buildLogicalOperator(
            OperatorStructure<S> structure, Function<S, LogicalOperatorDto<S, T>> creator) {

        LogicalOperatorDto<S, T> operator = creator.apply(structure.getSymbol());

        Stream<T> stream = structure.getChildren().stream().map(v -> {
            if (v instanceof OperatorStructure) {
                return (T) buildLogicalOperator((OperatorStructure<S>) v, creator);
            } else {
                return (T) v;
            }
        });
        return operator.addValues(stream);
    }

    public static <S, T> LogicalOperatorDto<S, T> buildLogicalOperator(
            List<Object> boolList, Function<S, LogicalOperatorDto<S, T>> creator, ToIntFunction<S> precedenceFunction) {
        OperatorStructure<S> structure = buildLogicalOperatorStructure(boolList, precedenceFunction);
        return buildLogicalOperator(structure, creator);
    }

    private static final class OperatorStructure<S> {
        private final S symbol;
        private final List<Object> children;

        private OperatorStructure(S symbol, Object... children) {
            this.symbol = symbol;
            this.children = Arrays.stream(children).collect(Collectors.toList());
        }

        public S getSymbol() {
            return symbol;
        }

        public List<Object> getChildren() {
            return children;
        }

        public void addChild(Object child) {
            children.add(child);
        }
    }

    private static <S> OperatorStructure<S> buildLogicalOperatorStructure(
            List<Object> boolList, ToIntFunction<S> precedenceFunction) {

        S currentSymbol = (S) boolList.get(1);
        OperatorStructure<S> currentOperator = new OperatorStructure(currentSymbol, boolList.get(0));

        OperatorStructure<S> lowestOperator = currentOperator;
        for (int i = 2, l = boolList.size(); i < l; i += 2) {
            S nextSymbol = i + 1 < l ? (S) boolList.get(i + 1) : null;
            Object currentOpValue = boolList.get(i);
            if (nextSymbol == null || Objects.equals(currentSymbol, nextSymbol)) {
                currentOperator.addChild(currentOpValue);

            } else {
                OperatorStructure<S> nextOperator = new OperatorStructure<>(nextSymbol);

                if (hasHigherPrecedence(currentSymbol, nextSymbol, precedenceFunction)) {
                    currentOperator.addChild(currentOpValue);
                    nextOperator.addChild(currentOperator);
                    lowestOperator = nextOperator;
                } else {
                    nextOperator.addChild(currentOpValue);
                    currentOperator.addChild(nextOperator);
                    lowestOperator = currentOperator;
                }

                currentOperator = nextOperator;
            }
            currentSymbol = nextSymbol;
        }
        return lowestOperator;
    }

    @Override
    public OrderByExpression visitOrderByExpr(AqlParser.OrderByExprContext ctx) {
        OrderByExpression orderByExpression = new OrderByExpression();
        orderByExpression.setStatement(visitIdentifiedPath(ctx.identifiedPath()));
        orderByExpression.setSymbol(extractSymbol(ctx));
        return orderByExpression;
    }

    private ConditionLogicalOperatorSymbol extractSymbolTerminal(TerminalNode child) {
        if (child == null) {
            return null;
        }

        switch (child.getSymbol().getText().toLowerCase(Locale.ROOT)) {
            case "or":
                return ConditionLogicalOperatorSymbol.OR;
            case "and":
                return ConditionLogicalOperatorSymbol.AND;
            case "xor":
                throw new AqlParseException("XOR not supported");
            default:
                return null;
        }
    }

    private static String unwrapText(RuleContext ctx) {
        return StringUtils.unwrap(StringUtils.unwrap(ctx.getText(), "'"), "\"");
    }

    private OrderByDirection extractSymbol(AqlParser.OrderByExprContext ctx) {
        if (ctx.DESC() != null || ctx.DESCENDING() != null) {
            return OrderByDirection.DESC;
        } else {
            return OrderByDirection.ASC;
        }
    }

    private ComparisonOperatorSymbol extractSymbol(TerminalNode comparisonOperator) {
        switch (comparisonOperator.getText()) {
            case "=":
                return ComparisonOperatorSymbol.EQ;
            case "!=":
                return ComparisonOperatorSymbol.NEQ;
            case ">":
                return ComparisonOperatorSymbol.GT;
            case ">=":
                return ComparisonOperatorSymbol.GT_EQ;
            case "<":
                return ComparisonOperatorSymbol.LT;
            case "<=":
                return ComparisonOperatorSymbol.LT_EQ;
            default:
                throw new AqlParseException("Unknown Token " + comparisonOperator.getText());
        }
    }

    private static <S> boolean hasHigherPrecedence(
            S operatorSymbol, S nextOperatorSymbol, ToIntFunction<S> precedenceFunction) {
        if (nextOperatorSymbol == null) {
            return true;
        } else {
            return precedenceFunction.applyAsInt(operatorSymbol) <= precedenceFunction.applyAsInt(nextOperatorSymbol);
        }
    }

    private ContainmentSetOperatorSymbol extractSymbol(AqlParser.ContainsExprContext ctx) {
        if (ctx == null) {
            return null;
        }
        if (ctx.OR() != null) {
            return ContainmentSetOperatorSymbol.OR;
        } else if (ctx.AND() != null) {
            return ContainmentSetOperatorSymbol.AND;
        } else {
            return null;
        }
    }

    private ConditionLogicalOperatorSymbol extractSymbol(AqlParser.WhereExprContext ctx) {
        if (ctx == null) {
            return null;
        }
        if (ctx.OR() != null) {
            return ConditionLogicalOperatorSymbol.OR;
        } else if (ctx.AND() != null) {
            return ConditionLogicalOperatorSymbol.AND;
        } else {
            return null;
        }
    }
}
