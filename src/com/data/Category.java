package com.data;

public enum Category {

    TV('1', "TV"), MOBILE('2', "Mobile");
    private char   m_categoryType;
    private String m_categoryName;

    Category(char categoryType, String categoryName) {
        m_categoryType = categoryType;
        m_categoryName = categoryName;
    }

    public char getCategoryType() {
        return m_categoryType;
    }

    public void setCategoryType(char categoryType) {
        m_categoryType = categoryType;
    }

    public String getCategoryName() {
        return m_categoryName;
    }

    public void setCategoryName(String categoryName) {
        m_categoryName = categoryName;
    }

    public static Category getInstance(String name) {
        if (name != null) {
            for (Category category : Category.values()) {
                if (category.getCategoryName().equalsIgnoreCase(name)) {
                    return category;
                }
            }
        }
        throw new RuntimeException("Invalid Category");
    }
}
