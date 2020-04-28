package com.andlinks.basic.utils;

import com.baisc.annonate.Excel;
import com.baisc.util.StrUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author yaLan
 * @date 2020/02/11 16:34
 * excel导出
 **/
@Slf4j
public class ExcelUtils<T> {
    public final static String EXCEL2003 = ".xls";//2003
    public final static String EXCEL2007 = ".xlsx";//2007+
    public final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * ImportExcel识别成对象
     * @param file   文件
     * @param clazz  识别对象类
     * @param headers  headers用来放类的属性，按照注解上的升序来
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> List<T> importExcel(File file, Class<T> clazz, String[] headers) throws Exception {
        List<T> list = null;
        //创建工作部
        Workbook wb = getWorkBook(file);
        if (null == wb) {
            throw new Exception("创建excel工作簿为空");
        }
        Sheet sheet = null;
        Row row = null;
        Cell cell = null;
        list = new ArrayList<>();
        //遍历excel中的所有sheet
//        for(int i=0;i<wb.getNumberOfSheets();i++){
//            sheet=wb.getSheetAt(i);
//            if(sheet==null){
//                continue;
//            }
//        }

        sheet = wb.getSheetAt(0);//跳出循环这里指定使用工作簿中的几个sheet表
        for (int j = sheet.getFirstRowNum(); j <= sheet.getLastRowNum(); j++) {
            row = sheet.getRow(j);
            if (row == null || row.getFirstCellNum() == j) {   //j是从0开始的，当第一行标题的时候不录入
                continue;
            }
            //遍历所有的列
            T t = clazz.newInstance();
            for (int k = 0; k < headers.length; k++) {
                cell = row.getCell(k);
                String mName = "set".concat(StrUtils.firstCharUpper(headers[k]));
                Method m = t.getClass().getDeclaredMethod(mName, String.class);
                m.invoke(t, getCellValue(cell));

            }

//            cell=
//            for (int y = row.getFirstCellNum(); y < row.getLastCellNum(); y++) {
//                cell = row.getCell(y);
//                li.add((T) getCellValue(cell));
//                                              }
            list.add(t);
        }
        return list;
    }

    /**
     * 根据list生成对应的excel文档
     * 第一步：写入表头
     * 第二步：遍历数据，每一个数据新起一行，在对应的javabean上添加ExcelDO的注解，为了顺序
     * 第三步：利用反射，得到当前行数据的所有fields，判断是否有注解ExcelDO，组成一个Field数组，利用java8的排序，根据bean上每个属性的值排序
     * 第四步：此时这个数组里的Field顺序对应excel生成的字段顺序，直接遍历field取值赋给cell里就ok
     * 第五步：利用流生成excel工作簿
     *
     * @param list    对象数组
     * @param generateName  生成文件名
     * @param header      第一行的列名，按注解升序来
     * @param <T>
     * @throws IllegalAccessException
     * @throws IOException
     */
    public static <T> void list2excel(List<T> list, String generateName, String[] header, HttpServletRequest request,  HttpServletResponse response) throws IllegalAccessException, IOException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
        HSSFRow row = sheet.createRow(0);//创建第一行
        HSSFCell cell = null;
        //步骤1：插入表头
        for (int i = 0; i < header.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(header[i]);
        }
        //步骤2：写入数据
        for (int i = 0; i < list.size(); i++) {
            row = sheet.createRow(i + 1);
            //步骤3：取得当前下标（行）的fields并根据注解里的顺序排序Field数组
            T t = list.get(i);
            Field[] f = t.getClass().getDeclaredFields();
            List<Field> withExcel = new ArrayList<>();
            for (int k = 0; k < f.length; k++) {
                if (null != f[k].getAnnotation(Excel.class)) {
                    withExcel.add(f[k]);
                }
            }
            withExcel.sort(Comparator.comparingInt(o -> o.getAnnotation(Excel.class).order()));//根据excel实体的注解顺序排序
            //步骤5：遍历排序后的Fields数组，利用反射取出属性值赋给cell
            for (int k = 0; k < withExcel.size(); k++) {
                Field orderField = withExcel.get(k);
                orderField.setAccessible(true);
                String value = (String) orderField.get(t);
                cell = row.createCell(k);
                cell.setCellValue(value);
            }
        }
        //步骤6：生成excel工作簿
    //本地生成
//        File file = new File(path);
//        file.createNewFile();
//        OutputStream out = new FileOutputStream(file);
//        workbook.write(out);
//        out.close();

        //后台接口生成
        try {
            generateName = encodeChineseDownloadFileName(request, generateName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.setHeader("Content-disposition", generateName);
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-disposition", "attachment;filename=" + generateName);
        response.setHeader("Pragma", "No-cache");
        OutputStream ouputStream = response.getOutputStream();
        workbook.write(ouputStream);
        ouputStream.flush();
        ouputStream.close();
    }


    /**
     * 描述：对表格中数值进行格式化
     *
     * @param cell
     * @return
     */
    public static String getCellValue(Cell cell) {
        //用String接收所有返回的值
        String value = null;
        DecimalFormat df = new DecimalFormat("0");  //格式化number String字符
        SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd");  //日期格式化
        DecimalFormat df2 = new DecimalFormat("0.00");  //格式化数字
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:  //String类型的数据
                value = cell.getStringCellValue();
                break;

            case Cell.CELL_TYPE_NUMERIC:   //数值类型(取值用cell.getNumericCellValue() 或cell.getDateCellValue())
                if ("General".equals(cell.getCellStyle().getDataFormatString())) {
                    value = df.format(cell.getNumericCellValue());
                } else if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    value = sdf.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
                } else {
                    value = df2.format(cell.getNumericCellValue());
                }
                break;

            case Cell.CELL_TYPE_BOOLEAN:  //Boolean类型
                value = String.valueOf(cell.getBooleanCellValue());
                break;


            case Cell.CELL_TYPE_FORMULA: //表达式类型
                value = String.valueOf(cell.getCellFormula());
                break;

            case Cell.CELL_TYPE_ERROR: //异常类型 不知道何时算异常
                value = String.valueOf(cell.getErrorCellValue());
                break;

            case Cell.CELL_TYPE_BLANK:  //空，不知道何时算空
                value = "";
                break;

            default:
                value = "";
                break;
        }
        if (value.equals("") || value == null) {
            value = "";
        }

        return value;
    }


    /**
     * 根据文件后缀名，自动生成
     *
     * @param file
     * @return
     * @throws Exception
     */
    private static Workbook getWorkBook(File file) {
        Workbook wb = null;
        String fileType = file.getName().substring(file.getName().lastIndexOf("."));
        try {
            if (EXCEL2003.equals(fileType)) {
                wb = new HSSFWorkbook(new FileInputStream(file));  //2003-
            } else if (EXCEL2007.equals(fileType)) {
                wb = new XSSFWorkbook(new FileInputStream(file));  //2007+
            } else {
                throw new Exception("解析的文件格式有误！");
            }
        } catch (Exception e) {
            log.error("生成工作簿错误");
        }
        return wb;
    }

    /**
     * 生成中文
     * @param request
     * @param pFileName
     * @return
     * @throws Exception
     */
    public static String encodeChineseDownloadFileName(HttpServletRequest request, String pFileName) throws Exception {
        String filename = null;
        String agent = request.getHeader("USER-AGENT");
        if (null != agent) {
            if (-1 != agent.indexOf("Firefox")) {//Firefox
                filename = "=?UTF-8?B?" + (new String(org.apache.commons.codec.binary.Base64.encodeBase64(pFileName.getBytes("UTF-8")))) + "?=";
            } else if (-1 != agent.indexOf("Chrome")) {//Chrome
                filename = new String(pFileName.getBytes(), "ISO8859-1");
            } else {//IE7+
                filename = java.net.URLEncoder.encode(pFileName, "UTF-8");
                filename = filename.replace("+", "%20");
            }
        } else {
            filename = pFileName;
        }
        return filename;
    }

}
