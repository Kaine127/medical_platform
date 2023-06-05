package com.mjs.medical.common;

public class UserExeception extends RuntimeException{

    private  CodeEnum codeEnum;

    public UserExeception(CodeEnum codeEnum) {this.codeEnum = codeEnum;}

    public CodeEnum getCodeEnum() {return codeEnum; }
}
