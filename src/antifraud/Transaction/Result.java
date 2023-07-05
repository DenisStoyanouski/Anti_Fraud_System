package antifraud.Transaction;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

public enum Result {
    ALLOWED,
    MANUAL_PROCESSING,
    PROHIBITED
}
