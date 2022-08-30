/*
 * Copyright (c) 2022 vitasystems GmbH and Hannover Medical School.
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
package org.ehrbase.serialisation.matrixencoding;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.lang3.StringUtils;
import org.ehrbase.aql.dto.path.AqlPath;
import org.ehrbase.util.exception.SdkException;

/**
 * @author Stefan Spiska
 */
public class FixedCodeSetEncoder implements Encoder {

    private final BiMap<String, String> totalEncoding = HashBiMap.create();
    private final BiMap<String, String> pathEncoding = HashBiMap.create();

    public FixedCodeSetEncoder() {

        try (CSVParser csv = new CSVParser(
                new InputStreamReader(FixedCodeSetEncoder.class.getResourceAsStream("/encoder/totalpathencoding.csv")),
                CSVFormat.DEFAULT.builder().setHeader("path", "code").build())) {
            csv.stream().forEach(c -> totalEncoding.put(c.get(0), c.get(1)));
        } catch (IOException e) {
            throw new SdkException(e.getMessage(), e);
        }

        try (CSVParser csv = new CSVParser(
                new InputStreamReader(FixedCodeSetEncoder.class.getResourceAsStream("/encoder/pathencoding.csv")),
                CSVFormat.DEFAULT.builder().setHeader("path", "code").build())) {
            csv.stream().forEach(c -> pathEncoding.put(c.get(0), c.get(1)));
        } catch (IOException e) {
            throw new SdkException(e.getMessage(), e);
        }
    }

    @Override
    public AqlPath encode(AqlPath path) {

        if (totalEncoding.containsKey(path.getPath())) {
            return AqlPath.parse(totalEncoding.get(path.getPath()));
        }

        AqlPath rootPath = AqlPath.ROOT_PATH;

        for (AqlPath.AqlNode aqlNode : path.getNodes()) {
            AqlPath.AqlNode n = encode(aqlNode);
            rootPath = rootPath.addEnd(n);
        }

        return rootPath;
    }

    private AqlPath.AqlNode encode(AqlPath.AqlNode node) {

        if (pathEncoding.containsKey(node.getName())) {

            return new AqlPath.AqlNode(pathEncoding.get(node.getName()), node.getAtCode(), node.getOtherPredicate());
        }

        return node;
    }

    @Override
    public AqlPath decode(AqlPath path) {

        String key = StringUtils.removeStart(path.getPath(), "/");

        if (totalEncoding.containsValue(key)) {
            return AqlPath.parse(totalEncoding.inverse().get(key));
        }

        AqlPath rootPath = AqlPath.ROOT_PATH;

        for (AqlPath.AqlNode aqlNode : path.getNodes()) {
            AqlPath.AqlNode n = decode(aqlNode);
            rootPath = rootPath.addEnd(n);
        }

        return rootPath;
    }

    private AqlPath.AqlNode decode(AqlPath.AqlNode node) {

        if (pathEncoding.containsValue(node.getName())) {

            return new AqlPath.AqlNode(
                    pathEncoding.inverse().get(node.getName()), node.getAtCode(), node.getOtherPredicate());
        }

        return node;
    }
}
