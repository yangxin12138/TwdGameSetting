package com.twd.gamesetting;

public class LanguageBean {
    private String LanguageName;
    private String LanguageCode;
    private boolean isSelect;

    public LanguageBean(String languageName, String languageCode, boolean isSelect) {
        LanguageName = languageName;
        LanguageCode = languageCode;
        this.isSelect = isSelect;
    }

    public String getLanguageName() {
        return LanguageName;
    }

    public void setLanguageName(String languageName) {
        LanguageName = languageName;
    }

    public String getLanguageCode() {
        return LanguageCode;
    }

    public void setLanguageCode(String languageCode) {
        LanguageCode = languageCode;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
