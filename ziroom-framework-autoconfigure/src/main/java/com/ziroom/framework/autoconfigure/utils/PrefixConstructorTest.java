///**
// * Copyright (c) 2008-2010 Andrey Somov
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.ziroom.framework.autoconfigure.utils;
//
//import org.yaml.snakeyaml.constructor.AbstractConstruct;
//import org.yaml.snakeyaml.constructor.BaseConstructor;
//import org.yaml.snakeyaml.constructor.SafeConstructor;
//import org.yaml.snakeyaml.nodes.Node;
//import org.yaml.snakeyaml.nodes.ScalarNode;
//import org.yaml.snakeyaml.nodes.SequenceNode;
//import org.yaml.snakeyaml.nodes.Tag;
//
//import java.util.List;
//
///**
// * Example to process a family of tags with the same prefix with one constructor
// * (PrefixConstruct)
// */
//public class PrefixConstructorTes {
//
//
//    private class CustomConstructor extends SafeConstructor {
//        public CustomConstructor() {
//            // define tags which begin with !org.yaml.
//            String prefix = "!org.yaml.";
//            this.yamlMultiConstructors.put(prefix, new PrefixConstruct(prefix,
//                    CustomConstructor.this));
//            this.yamlConstructors.put(null, new ConstructUnknown(CustomConstructor.this));
//            this.yamlConstructors.put(new Tag("!org.yaml.Exact"), new ExactConstruct(
//                    CustomConstructor.this));
//        }
//    }
//
//    /**
//     * Process tags which start with '!org.yaml.'
//     */
//    private class PrefixConstruct extends AbstractConstruct {
//        private String prefix;
//        private BaseConstructor con;
//
//        public PrefixConstruct(String prefix, BaseConstructor con) {
//            this.prefix = prefix;
//            this.con = con;
//        }
//
//        public Object construct(Node node) {
//            String suffix = node.getTag().getValue().substring(prefix.length());
//            return new Extra(suffix, con.constructScalar((ScalarNode) node).toString());
//        }
//    }
//
//    /**
//     * This has more priority then PrefixConstruct
//     */
//    private class ExactConstruct extends AbstractConstruct {
//        private BaseConstructor con;
//
//        public ExactConstruct(BaseConstructor con) {
//            this.con = con;
//        }
//
//        public Object construct(Node node) {
//            return new Extra("Item", con.constructScalar((ScalarNode) node).toString());
//        }
//    }
//
//    /**
//     * Process unrecognised tags
//     */
//    private class ConstructUnknown extends AbstractConstruct {
//        private BaseConstructor con;
//
//        public ConstructUnknown(BaseConstructor con) {
//            this.con = con;
//        }
//
//        @SuppressWarnings("unchecked")
//        public Object construct(Node node) {
//            List<String> list = (List<String>) con.constructSequence((SequenceNode) node);
//            return new Extra(list.get(0), list.get(1));
//        }
//    }
//
//    private class Extra {
//        private String name;
//        private String value;
//
//        public Extra(String name, String value) {
//            this.name = name;
//            this.value = value;
//        }
//
//        public String getValue() {
//            return value;
//        }
//
//        public String getName() {
//            return name;
//        }
//    }
//}