package com.sin.cashgift.utils

import android.os.Environment
import com.sin.cashgift.App
import com.sin.cashgift.data.CashBean
import com.sin.cashgift.data.CashDatabase
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

object ExcelParseUtils {
    private val XLS_EXCEL_FILE_PATH =
        Environment.getExternalStorageDirectory().absolutePath + File.separator + "/lj.xls"
    private val XLSX_EXCEL_FILE_PATH =
        Environment.getExternalStorageDirectory().absolutePath + File.separator + "/lj.xlsx"

    private val columns = listOf("姓名", "礼金")
    private val propertyNames = listOf(
        "name", "money")
    private var id = 0L
    fun readExcel(): List<CashBean>? {
        LogUtil.logE("TAG","本地解析list")
        var sheet: Sheet? = null
        var row: Row? = null
        var rowHeader: Row? = null
        var list: MutableList<CashBean>? = null
        var cellData = ""
        var wb: Workbook? = null
        val xlsFile = File(XLS_EXCEL_FILE_PATH)
        val xlsxFile = File(XLSX_EXCEL_FILE_PATH)
        val excelFile = (when {
            xlsFile.exists() -> {
                xlsFile
            }
            xlsxFile.exists() -> {
                xlsxFile
            }
            else -> {
                null
            }
        })
            ?: return null
        var inputStream: InputStream? = null
        try {
            val xlsInputStream = getAssetInputStream()
            val xlsxInputStream = getAssetInputStream(false)
            inputStream = xlsInputStream
                ?: (xlsxInputStream ?: FileInputStream(excelFile))
            wb = when {
                excelFile.absolutePath.endsWith(".xls") -> {
                    HSSFWorkbook(inputStream)
                }
                excelFile.absolutePath.endsWith(".xlsx") -> {
                    XSSFWorkbook(inputStream)
                }
                else -> {
                    null
                }
            }
            if (wb == null) return null
            //用于存放excel数据
            list = mutableListOf()
            //获取第一个sheet
            sheet = wb.getSheetAt(0)
            //获取最大行数
            val rowNum = sheet.physicalNumberOfRows
            //获取第一行
            rowHeader = sheet.getRow(0)
            row = sheet.getRow(0)
            //获取最大列数
            val colNum = row.physicalNumberOfCells
            for (i in 1 until rowNum) {
                val map = linkedMapOf<String, String>()
                row = sheet.getRow(i)
                if (row != null) {
                    for (j in 0 until colNum) {
                        if (columns[j] == getCellFormatValue(rowHeader.getCell(j))) {
                            cellData = getCellFormatValue(row.getCell(j))
                            map[propertyNames[j]] = cellData
                        }
                    }
                } else {
                    break
                }
                val trainDataBean = CashBean(id)
                convertMapToBean(trainDataBean, map)
                list.add(trainDataBean)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return list
    }


    private var simpleDateFormat: SimpleDateFormat? = null
    private fun getDateFormat(pattern: String): SimpleDateFormat {
        if (simpleDateFormat == null) {
            simpleDateFormat = SimpleDateFormat(pattern, Locale.CHINA)
        }
        return simpleDateFormat!!
    }

    private fun getAssetInputStream(isGetXls:Boolean = true):InputStream?{
        return try {
            if (isGetXls){
                App.mContext.assets.open("lj.xls")
            }else{
                App.mContext.assets.open("lj.xlsx")
            }
        }catch (e:Exception){
            null
        }
    }

    private fun getCellFormatValue(cell: Cell?): String {
        return if (cell == null) {
            ""
        } else {
            when (cell.cellType) {
                Cell.CELL_TYPE_NUMERIC -> {
                    val dataFormat = cell.cellStyle.dataFormat.toInt()
                    if (DateUtil.isCellDateFormatted(cell)) {
                        val simpleDateFormat = if (dataFormat == 20 || dataFormat == 32) {
                            getDateFormat("HH:mm")
                        } else if (dataFormat == 14 || dataFormat == 31 || dataFormat == 57 || dataFormat == 58) {
                            getDateFormat("yyyy-MM-dd")
                        } else {
                            getDateFormat("yyyy-MM-dd HH:mm")
                        }
                        try {
                            simpleDateFormat.format(cell.dateCellValue)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            ""
                        }
                    } else {
                        cell.numericCellValue.toString().replace(".0", "")
                    }
                }
                Cell.CELL_TYPE_STRING -> {
                    cell.stringCellValue
                }
                else -> {
                    ""
                }
            }
        }
    }

    private fun convertMapToBean(cashBean: CashBean, map: Map<String, String>) {
        val cls = cashBean.javaClass
        val entries = map.entries
        propertyNames.forEach { propertyName ->
            entries.forEach en@{
                if (propertyName == it.key) {
                    setFieldValue(cls, propertyName, cashBean, it.value)
                    return@en
                }
            }
        }
        CashDatabase.getInstance(App.mContext).cashDao().saveCash(cashBean)
        LogUtil.logE("TAG","saveBean: $cashBean")
        id++
    }

    private fun setFieldValue(
        cls: Class<CashBean>,
        propertyName: String,
        cashBean: CashBean,
        value: String
    ) {
        val field = cls.getDeclaredField(propertyName)
        field.isAccessible = true
        field.set(cashBean, value)
    }
}