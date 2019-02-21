package com.main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.html.HTMLDocument.HTMLReader.TagAction;

import com.data.Category;
import com.data.ItemDetails;
import com.data.ItemInfo;
import com.data.SubCategory;
import com.data.TaxInfo;
import com.util.CSVUtil;

public class CSVReader {
	public static final String DELIM = ",";
	public static int decimalPlaces = 2;
	public static BigDecimal bigDecimal;


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
					}else {
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
						double perchaseTax1Computed = (itemInfo.getPurchasePrice() * tax1Perc)/100D;
						double perchaseTax2Computed = (perchaseTax1Computed * tax2Perc)/100D;
						double totalTax = Math.round(perchaseTax1Computed + perchaseTax2Computed);
						totalTax = roundingOffTheValue(totalTax);


						double gain = itemInfo.getSalePrice() - (itemInfo.getPurchasePrice() + totalTax);
						gain = roundingOffTheValue(gain);

						double gainPerc = Math.round((gain / itemInfo.getPurchasePrice()) * 100);
						gainPerc = roundingOffTheValue(gainPerc);

						map.put(itemInfo.getSubCategory(), new ItemDetails(itemInfo.getCategory(), itemInfo.getSubCategory(), itemInfo.getPurchasePrice(),
								totalTax, itemInfo.getSalePrice(), gain, gainPerc));
					}
				}
			}
		}
		// generate Output CSV
		if (!itemFile.isEmpty() && !taxFile.isEmpty()) {
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
					List<String> values =  new ArrayList<String>();
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
		double perchaseTax1Computed = (itemInfo.getPurchasePrice() * tax1Perc)/100;
		double perchaseTax2Computed = (perchaseTax1Computed * tax2Perc)/100;
		double totalTax = Math.round((perchaseTax1Computed + perchaseTax2Computed)*100.0)/100.0;
		totalTax = roundingOffTheValue(totalTax);

		double gain = itemInfo.getSalePrice() - (itemInfo.getPurchasePrice()+totalTax);
		gain = roundingOffTheValue(gain);

		double gainPerc = (((gain / itemInfo.getPurchasePrice()) * 100)*100.0)/100.0;
		gainPerc = roundingOffTheValue(gainPerc);

		double taxInclusiveExisting = totalTax + existingDetails.getTotalTaxes();
		double totalPerchasePrice = itemInfo.getPurchasePrice() + existingDetails.getTotalPurchase();
		double totalSalesPrice = itemInfo.getSalePrice() + existingDetails.getTotalSalePrice();
		double totalGain = gain + existingDetails.getGain();
		double totalGainPerc = (totalGain/totalPerchasePrice)*100;
		totalGainPerc = roundingOffTheValue(totalGainPerc);

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
		double perchaseTax1Computed = (itemInfo.getPurchasePrice() * tax1Perc)/100D;
		double perchaseTax2Computed = (perchaseTax1Computed * tax2Perc)/100D;
		double totalTax = Math.round(perchaseTax1Computed + perchaseTax2Computed);
		totalTax = roundingOffTheValue(totalTax);


		double gain = itemInfo.getSalePrice() - (itemInfo.getPurchasePrice() + totalTax);
		gain = roundingOffTheValue(gain);

		double gainPerc = Math.round((gain / itemInfo.getPurchasePrice()) * 100);
		gainPerc = roundingOffTheValue(gainPerc);

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
					taxInfos.add(new TaxInfo(taxInfo[0].replace("\"", "").trim(), taxInfo[1].trim(), Double.parseDouble(taxInfo[2].replace("\"", "").trim()),
							Double.parseDouble(taxInfo[3].replace("\"", "").trim())));
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

	private static void getItemInfo(String itemFile, List<ItemInfo> items) {
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
				String[] itemInfo = line.split(DELIM);
				if (itemInfo != null && itemInfo.length == 5) {
					items.add(new ItemInfo(itemInfo[0].replace("\"", ""), itemInfo[1], itemInfo[2], Double.parseDouble(itemInfo[3]),
							Double.parseDouble(itemInfo[4].replace("\"", ""))));

				}
				iteration++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	//rounding Off the Values
	private static double roundingOffTheValue(double valueToBeConvertedInNumberFormat) {
		bigDecimal = new BigDecimal(valueToBeConvertedInNumberFormat);
		return (bigDecimal.setScale(decimalPlaces, bigDecimal.ROUND_HALF_UP).doubleValue());
	}


}
