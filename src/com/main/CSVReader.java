package com.main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.data.Category;
import com.data.ItemDetails;
import com.data.ItemInfo;
import com.data.SubCategory;
import com.data.TaxInfo;
import com.util.CSVUtil;

public class CSVReader {
	public static final String DELIM = ",";

	public static void main(String[] args) {

		String itemFile = System.getProperty("user.dir") + "\\items.csv";
		String taxFile = System.getProperty("user.dir") + "\\tax.csv";
		/*
		 * if (args != null && args.length > 0) { itemFile = args[0]; } if (args != null
		 * && args.length > 1) { taxFile = args[1]; }
		 */
		List<ItemInfo> items = new ArrayList<ItemInfo>();
		List<TaxInfo> taxInfos = new ArrayList<TaxInfo>();

		if (itemFile != null) {
			getItemInfo(itemFile, items);
		}
		if (taxFile != null) {

			getTaxInfo(taxFile, taxInfos);
		}
		Map<Category, Map<SubCategory, ItemDetails>> categoryToSubCategoryItemDetailsMap = new HashMap<Category, Map<SubCategory, ItemDetails>>();
		if (!items.isEmpty() && !taxInfos.isEmpty()) {
			for (ItemInfo itemInfo : items) {
				Map<SubCategory, ItemDetails> map = categoryToSubCategoryItemDetailsMap.get(itemInfo.getCategory());
				if (map == null) {
					computeAndSetDetails(taxInfos, categoryToSubCategoryItemDetailsMap, itemInfo);
				} else {
					if (map.get(itemInfo.getSubCategory()) != null) {
						computeAndAddExistingInfo(taxInfos, itemInfo, map);
					}
				}
			}
		}
		// generate Output CSV
		if (args.length > 2) {
			writeToCSV(args, categoryToSubCategoryItemDetailsMap);
		}
	}





	private static void writeToCSV(String[] args,
			Map<Category, Map<SubCategory, ItemDetails>> categoryToSubCategoryItemDetailsMap) {
		String outputFilePath = System.getProperty("user.dir")+"\\gain.csv";
		try {
			FileWriter writer = new FileWriter(outputFilePath);
			ArrayList<String> header = new ArrayList<String>();
			header.add("Category");
			header.add("Sub-Category");
			header.add("Total Purchase Price");
			header.add("Total Tax (Rounded Figure)");
			header.add("Sale Price");
			header.add("Gain (Sale-(Purchase + Tax))");
			header.add("Gain %");
			CSVUtil.writeLine(writer, header);
			for (Map<SubCategory, ItemDetails> details : categoryToSubCategoryItemDetailsMap.values()) {
				for (ItemDetails itemInfo : details.values()) {
					List<String> values = header;
					values.add(itemInfo.getCategory().getCategoryName());
					values.add(itemInfo.getSubCategory().getDisplayString());
					values.add(String.valueOf(itemInfo.getTotalPurchase()));
					values.add(String.valueOf(itemInfo.getTotalTaxes()));
					values.add(String.valueOf(itemInfo.getTotalSalePrice()));
					values.add(String.valueOf(itemInfo.getGain()));
					values.add(String.valueOf(itemInfo.getGainPercent()));
					CSVUtil.writeLine(writer, values);
				}
			}
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void computeAndAddExistingInfo(List<TaxInfo> taxInfos, ItemInfo itemInfo,
			Map<SubCategory, ItemDetails> map) {
		ItemDetails existingDetails = map.get(itemInfo.getSubCategory());
		double tax1Perc = 0.0;
		double tax2Perc = 0.0;
		for (TaxInfo taxInfo : taxInfos) {
			if (taxInfo.getCategory() == itemInfo.getCategory()
					&& taxInfo.getSubCategory() == itemInfo.getSubCategory()) {
				tax1Perc = taxInfo.getTax1Percentage();
				tax2Perc = taxInfo.getTax2Percentage();
				break;
			}
		}
		double perchaseTax1Computed = itemInfo.getPurchasePrice() * tax1Perc;
		double perchaseTax2Computed = perchaseTax1Computed * tax2Perc;
		double totalTax = Math.round(perchaseTax1Computed + perchaseTax2Computed);

		double salesPriceTax1Computed = itemInfo.getSalePrice() * tax1Perc;
		double salesPriceTax2Computed = salesPriceTax1Computed * tax2Perc;
		double totalSalesPriceTax = Math.round(salesPriceTax1Computed + salesPriceTax2Computed);

		double gain = totalSalesPriceTax - (itemInfo.getPurchasePrice() + totalTax);
		double gainPerc = (gain / itemInfo.getPurchasePrice()) * 100;

		double taxInclusiveExisting = totalTax + existingDetails.getTotalTaxes();
		double totalPerchasePrice = itemInfo.getPurchasePrice() + existingDetails.getTotalPurchase();
		double totalSalesPrice = itemInfo.getSalePrice() + existingDetails.getTotalSalePrice();
		double totalGain = gain + existingDetails.getGain();
		double totalGainPerc = gainPerc + existingDetails.getGainPercent();
		map.put(itemInfo.getSubCategory(), new ItemDetails(itemInfo.getCategory(), itemInfo.getSubCategory(),
				totalPerchasePrice, taxInclusiveExisting, totalSalesPrice, totalGain, totalGainPerc));
	}

	private static void computeAndSetDetails(List<TaxInfo> taxInfos,
			Map<Category, Map<SubCategory, ItemDetails>> categoryToSubCategoryItemDetailsMap, ItemInfo itemInfo) {
		HashMap<SubCategory, ItemDetails> subCatToItemDetailsMap = new HashMap<SubCategory, ItemDetails>();
		categoryToSubCategoryItemDetailsMap.put(itemInfo.getCategory(), subCatToItemDetailsMap);
		double tax1Perc = 0.0;
		double tax2Perc = 0.0;
		for (TaxInfo taxInfo : taxInfos) {
			if (taxInfo.getCategory() == itemInfo.getCategory()
					&& taxInfo.getSubCategory() == itemInfo.getSubCategory()) {
				tax1Perc = taxInfo.getTax1Percentage();
				tax2Perc = taxInfo.getTax2Percentage();
				break;
			}
		}
		double perchaseTax1Computed = itemInfo.getPurchasePrice() * tax1Perc;
		double perchaseTax2Computed = perchaseTax1Computed * tax2Perc;
		double totalTax = Math.round(perchaseTax1Computed + perchaseTax2Computed);

		double salesPriceTax1Computed = itemInfo.getSalePrice() * tax1Perc;
		double salesPriceTax2Computed = salesPriceTax1Computed * tax2Perc;
		double totalSalesPriceTax = Math.round(salesPriceTax1Computed + salesPriceTax2Computed);

		double gain = totalSalesPriceTax - (itemInfo.getPurchasePrice() + totalTax);
		double gainPerc = (gain / itemInfo.getPurchasePrice()) * 100;

		subCatToItemDetailsMap.put(itemInfo.getSubCategory(),
				new ItemDetails(itemInfo.getCategory(), itemInfo.getSubCategory(), itemInfo.getPurchasePrice(),
						totalTax, itemInfo.getSalePrice(), gain, gainPerc));
	}

	private static void getTaxInfo(String itemFile, List<TaxInfo> taxInfos) {
		BufferedReader br = null;
		String line = "";
		int iteration = 0;
		try {

			br = new BufferedReader(new FileReader(itemFile));
			while ((line = br.readLine()) != null) {
				if(iteration==0) {
					iteration++;
					continue;
				}
				
				String[] taxInfo = line.split(DELIM);
				if (taxInfo != null && taxInfo.length == 4) {
					taxInfos.add(new TaxInfo(taxInfo[0], taxInfo[1], Double.parseDouble(taxInfo[2]),
							Double.parseDouble(taxInfo[3])));
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static void getItemInfo(String taxFile, List<ItemInfo> items) {
		BufferedReader br = null;
		String line = "";
		int iteration = 0;
		try {

			br = new BufferedReader(new FileReader(taxFile));

			while ((line = br.readLine()) != null) {
				if(iteration==0) {
					iteration++;
					continue;
				}
				System.out.println(line);
				String[] itemInfo = line.split(DELIM);
				if (itemInfo != null && itemInfo.length == 5) {
					items.add(new ItemInfo(itemInfo[0], itemInfo[1], itemInfo[2], Double.parseDouble(itemInfo[3]),
							Double.parseDouble(itemInfo[4])));
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
