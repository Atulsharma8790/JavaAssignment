package com.data;

public class ItemDetails {
    private Category    m_category;

    private SubCategory m_subCategory;
    private double      m_totalPurchase;
    private double      m_totalTaxes;
    private double      m_totalSalePrice;
    private double      m_gain;
    private double      m_gainPercent;

    public ItemDetails(Category category, SubCategory subCategory, double totalPurchase, double totalTaxes,
            double totalSalePrice, double gain, double gainPercent) {
        super();
        m_category = category;
        m_subCategory = subCategory;
        m_totalPurchase = totalPurchase;
        m_totalTaxes = totalTaxes;
        m_totalSalePrice = totalSalePrice;
        m_gain = gain;
        m_gainPercent = gainPercent;
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

    public double getTotalPurchase() {
        return m_totalPurchase;
    }

    public void setTotalPurchase(double totalPurchase) {
        m_totalPurchase = totalPurchase;
    }

    public double getTotalTaxes() {
        return m_totalTaxes;
    }

    public void setTotalTaxes(double totalTaxes) {
        m_totalTaxes = totalTaxes;
    }

    public double getTotalSalePrice() {
        return m_totalSalePrice;
    }

    public void setTotalSalePrice(double totalSalePrice) {
        m_totalSalePrice = totalSalePrice;
    }

    public double getGain() {
        return m_gain;
    }

    public void setGain(double gain) {
        m_gain = gain;
    }

    public double getGainPercent() {
        return m_gainPercent;
    }

    public void setGainPercent(double gainPercent) {
        m_gainPercent = gainPercent;
    }

}
