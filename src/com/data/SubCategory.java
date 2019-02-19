package com.data;

public enum SubCategory {
    PLAZMA('1', "Plazma"), LCD('2', "LCD"), SAMSUNG('3', "Samsung"), LENOVA('4', "Lenova");
    private char   m_subCategory;
    private String m_displayString;

    SubCategory(char subCategory, String displayString) {
        m_subCategory = subCategory;
        m_displayString = displayString;
    }

    public static SubCategory getInstance(String name) {
        if (name != null) {
            for (SubCategory category : SubCategory.values()) {
                if (category.getDisplayString().equalsIgnoreCase(name)) {
                    return category;
                }
            }
        }
        throw new RuntimeException("Invalid Sub Category");
    }

    public char getSubCategory() {
        return m_subCategory;
    }

    public void setSubCategory(char subCategory) {
        m_subCategory = subCategory;
    }

    public String getDisplayString() {
        return m_displayString;
    }

    public void setDisplayString(String displayString) {
        m_displayString = displayString;
    }
}
