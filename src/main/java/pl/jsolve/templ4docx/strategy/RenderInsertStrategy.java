package pl.jsolve.templ4docx.strategy;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import pl.jsolve.templ4docx.insert.Insert;
import pl.jsolve.templ4docx.insert.RenderInsert;
import pl.jsolve.templ4docx.insert.TextInsert;
import pl.jsolve.templ4docx.variable.RenderVariable;
import pl.jsolve.templ4docx.variable.TextVariable;
import pl.jsolve.templ4docx.variable.Variable;

import java.util.Objects;

public class RenderInsertStrategy implements InsertStrategy {

    @Override
    public void insert(Insert insert, Variable variable) {
        if (!(insert instanceof RenderInsert)) {
            return;
        }
        if (!(variable instanceof RenderVariable)) {
            return;
        }

        RenderInsert renderInsert = (RenderInsert) insert;
        RenderVariable renderVariable = (RenderVariable) variable;

        for (XWPFRun run : renderInsert.getParagraph().getRuns()) {
            XWPFParagraph currentParagraph = (XWPFParagraph) run.getParent();
            String text = run.getText(0);
            if (StringUtils.contains(text, renderInsert.getKey().getKey())) {
                if (CollectionUtils.isNotEmpty(renderVariable.getValues())){
                    boolean unbrokenTable = false;
                    for (Object value : renderVariable.getValues()) {
                        XmlCursor cursor = currentParagraph.getCTP().newCursor();
                        if (value instanceof RenderVariable.Paragraph){
                            XWPFParagraph newParagraph = run.getDocument().insertNewParagraph(cursor);
                            if (Objects.isNull(newParagraph)){
                                // 不支持在表格中使用该种占位符，整个段落无需处理
                                return;
                            }
                            XWPFRun newRun = newParagraph.createRun();
                            newRun.setText(((RenderVariable.Paragraph) value).getText());
                            unbrokenTable = false;
                        } else if (value instanceof RenderVariable.Table) {
                            if (unbrokenTable){
                                // 处理连续表格
                                XmlCursor unbrokenTableCursor = currentParagraph.getCTP().newCursor();
                                run.getDocument().insertNewParagraph(unbrokenTableCursor);
                                unbrokenTableCursor.dispose();
                            }
                            XWPFTable newTable = run.getDocument().insertNewTbl(currentParagraph.getCTP().newCursor());
                            if (Objects.isNull(newTable)){
                                // 不支持在表格中使用该种占位符，整个段落无需处理
                                return;
                            }
                            unbrokenTable = true;

                            int rowSize = ((RenderVariable.Table) value).getRowsAndCells().size();
                            int cellSize = ((RenderVariable.Table) value).getRowsAndCells().get(0).size();

                            // 初始化表格行列
                            for (int r = 0; r < rowSize; r++) {
                                if (r == 0){
                                    for (int c = 0; c < cellSize - 1; c++) {
                                        newTable.getRow(0).createCell();
                                    }
                                }else {
                                    newTable.createRow();
                                }
                            }

                            // 写入表格数据
                            for (int r = 0; r < rowSize; r++) {
                                for (int c = 0; c < cellSize; c++) {
                                    XWPFTableCell cell = newTable.getRow(r).getCell(c);
                                    String cellText = ((RenderVariable.Table) value).getRowsAndCells().get(r).get(c);
                                    cell.setText(cellText);
                                }
                            }
                        }
                        cursor.dispose();
                    }
                }

                text = StringUtils.replace(text, renderInsert.getKey().getKey(), "");
                run.setText(text, 0);
            }
        }
    }
}
