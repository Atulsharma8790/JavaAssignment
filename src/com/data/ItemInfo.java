package com.data;

public class ItemInfo {
    private Category    m_category;
    private SubCategory m_subCategory;
    private String      m_id;
    private double      m_purchasePrice;
    private double      m_salePrice;

    public ItemInfo(String categoryName, String subCategoryName, String id, double purchasePrice, double salePrice) {
        super();
        m_category = Category.getInstance(categoryName);
        m_subCategory = SubCategory.getInstance(subCategoryName);
        m_id = id;
        m_purchasePrice = purchasePrice;
        m_salePrice = salePrice;
    }

    public ItemInfo(Category category, SubCategory subCategory, String id, double purchasePrice, double salePrice) {
        super();
        m_category = category;
        m_subCategory = subCategory;
        m_id = id;
        m_purchasePrice = purchasePrice;
        m_salePrice = salePrice;
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

    public String getId() {
        return m_id;
    }

    public void setId(String id) {
        m_id = id;
    }

    public double getPurchasePrice() {
        return m_purchasePrice;
    }

    public void setPurchasePrice(double purchasePrice) {
        m_purchasePrice = purchasePrice;
    }

    public double getSalePrice() {
        return m_salePrice;
    }

    public void setSalePrice(double salePrice) {
        m_salePrice = salePrice;
    }
}
