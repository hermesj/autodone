package de.uoc.dh.idh.autodone.utils;

public enum Visibility {
    PUBLIC {
        @Override
        public String toString() {
            return "public";
        }
    },
    PRIVATE {
        @Override
        public String toString() {
            return "private";
        }
    },
    UNLISTED {
        @Override
        public String toString() {
            return "unlisted";
        }
    },
    DIRECT {
        @Override
        public String toString() {
            return "direct";
        }
    }
}