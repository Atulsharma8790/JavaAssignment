package com.data;

public class TaxInfo {

    private Category    m_category;
    private SubCategory m_subCategory;
    private double      m_tax1Percentage;
    private double      m_tax2Percentage;

    public TaxInfo(String category, String subCategory, double tax1Percentage, double tax2Percentage) {
        super();
        m_category = Category.getInstance(category);
        m_subCategory = SubCategory.getInstance(subCategory);
        m_tax1Percentage = tax1Percentage;
        m_tax2Percentage = tax2Percentage;
    }

    public TaxInfo(Category category, SubCategory subCategory, double tax1Percentage, double tax2Percentage) {
        super();
        m_category = category;
        m_subCategory = subCategory;
        m_tax1Percentage = tax1Percentage;
        m_tax2Percentage = tax2Percentage;
    }

    public Category getCategory() {
        return m_category;
    }

    public void setCategory(Category category) {
        m_category = category;
    }

    public SubCategory getSubCategory() {
        return m_subCategory;
    }

    public void setSubCategory(SubCategory subCategory) {
        m_subCategory = subCategory;
    }

    public double getTax1Percentage() {
        return m_tax1Percentage;
    }

    public void setTax1Percentage(double tax1Percentage) {
        m_tax1Percentage = tax1Percentage;
    }

    public double getTax2Percentage() {
        return m_tax2Percentage;
    }

    public void setTax2Percentage(double tax2Percentage) {
        m_tax2Percentage = tax2Percentage;
    }

}
