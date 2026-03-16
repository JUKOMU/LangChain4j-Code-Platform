package io.github.jukomu.langchain4jcodeplatform.model.enums;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

/**
 * @author JUKOMU
 * @Description: 用户角色枚举
 * @Project: LangChain4j-Code-Platform
 * @Date: 2026/3/16
 */
@Getter
public enum UserRoleEnum {

    ADMIN("管理员", "ADMIN"),
    USER("用户", "USER");

    private final String text;
    private final String value;

    UserRoleEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value 枚举值的value
     * @return 枚举值
     */
    public static UserRoleEnum getByValue(String value) {
        if (ObjectUtil.isNull(value)) {
            return null;
        }
        for (UserRoleEnum e : UserRoleEnum.values()) {
            if (e.value.equalsIgnoreCase(value)) {
                return e;
            }
        }
        return null;
    }
}
