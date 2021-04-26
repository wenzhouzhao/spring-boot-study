package cloud.codecloud.enums;

/**
 * 英文标点符号枚举
 *
 * @author zhaoYoung
 * @date 2021/4/25 21:51
 */
public enum PunctuationEnum {
    /**
     * 英文逗号[,]
     */
    COMMA(",", "逗号"),
    /**
     * 英文分号[;]
     */
    SEMICOLON(";", "分号"),
    /**
     * 英文冒号[:]
     */
    COLON(":", "冒号"),
    /**
     * 英文句号[.]
     */
    PERIOD(".", "句号"),
    /**
     * 英文横线[-]
     */
    HORIZONTAL_LINE("-","横线"),
    /**
     * 英文斜杠[/]
     */
    SLASH("/","斜杠"),
    /**
     * HTML换行符[<br/>]
     */
    BR("<br/>","HTML换行符"),
    /**
     * 右箭头[->]
     */
    RIGHT_ARROW("->","右箭头"),
    /**
     * 顿号[、]
     */
    TON("、","顿号"),
    /**
     * 百分号[%]
     */
    PERCENT_SIGN("%","百分号"),
    /**
     * 星号[*]
     */
    ASTERISK("*","星号"),
    /**
     * 姓名中间的[·]
     */
    NAME_CENTER_POINT("·",""),
    /**
     * 下划线[_]
     */
    UNDERLINE("_","下划线"),
    /**
     * 一个空格[ ]
     */
    ONE_SPACE(" ",""),

    /**
     * 英文问号[?]
     */
    QUESTION_MARK("?","英文问号")

    ;

    private String code;

    private String desc;

    PunctuationEnum(String code,String desc){
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
