package com.asiscontrol.security;

import com.asiscontrol.exception.BusinessRuleException;

import java.nio.charset.StandardCharsets;

public final class PasswordPolicy {
    private PasswordPolicy() {}

    public static void validate(String password, String username) {
        if (password == null
                || password.length() < 8
                || password.length() > 64
                || password.getBytes(StandardCharsets.UTF_8).length > 72
                || password.codePoints()
                        .anyMatch(
                                c ->
                                        Character.isWhitespace(c)
                                                || Character.isSpaceChar(c)
                                                || Character.isISOControl(c))
                || !password.codePoints().anyMatch(Character::isUpperCase)
                || !password.codePoints().anyMatch(Character::isLowerCase)
                || !password.codePoints().anyMatch(Character::isDigit)
                || password.equalsIgnoreCase(username))
            throw new BusinessRuleException(
                    "CONTRASENA_INVALIDA",
                    "Use de 8 a 64 caracteres, mayúscula, minúscula y número, sin espacios; no use"
                        + " su usuario (máximo 72 bytes UTF-8)");
    }
}
