package com.sin.cashgift.utils

import android.os.Environment
import com.sin.cashgift.data.TrainDataBeanGuiYang
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
        Environment.getExternalStorageDirectory().absolutePath + File.separator + "/show.xls"
    private val XLSX_EXCEL_FILE_PATH =
        Environment.getExternalStorageDirectory().absolutePath + File.separator + "/show.xlsx"
//    private val columns = listOf("序号", "车次","站台", "运行区段", "开检时间", "停检时间", "开点", "待车", "检票口")
//    private val propertyNames = listOf(
//        "number", "trainName","stationName",
//        "operationLine", "startCheckTime", "stopCheckTime", "launchTime", "waitColor", "checkPort"
//    )

    private val columns = listOf("序号", "车次","站台", "运行区段","待车")
    private val propertyNames = listOf(
        "number", "trainName","stationName",
        "operationLine", "waitColor")
    fun readExcel(): MutableList<TrainDataBeanGuiYang>? {
        LogUtil.logE("TAG","本地解析list")
        var sheet: Sheet? = null
        var row: Row? = null
        var rowHeader: Row? = null
        var list: MutableList<TrainDataBeanGuiYang>? = null
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
            inputStream = FileInputStream(excelFile)
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
                val trainDataBean = TrainDataBeanGuiYang()
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

    private fun convertMapToBean(trainDataBean: TrainDataBeanGuiYang, map: Map<String, String>) {
        val cls = trainDataBean.javaClass
        val entries = map.entries
        propertyNames.forEach { propertyName ->
            entries.forEach en@{
                if (propertyName == it.key) {
                    setFieldValue(cls, propertyName, trainDataBean, it.value)
                    return@en
                }
            }
        }
        //AppDatabase.instance.getDao().saveTrainData(trainDataBean)
    }

    private fun setFieldValue(
        cls: Class<TrainDataBeanGuiYang>,
        propertyName: String,
        trainDataBean: TrainDataBeanGuiYang,
        value: String
    ) {
        val field = cls.getDeclaredField(propertyName)
        field.isAccessible = true
        field.set(trainDataBean, value)
    }
}