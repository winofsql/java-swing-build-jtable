package lightbox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.*;
import java.util.*;
import javax.swing.event.*;
import java.util.Timer;
import java.util.TimerTask;


public class LboxTable extends JTable {

private static final long serialVersionUID = 1L;
public JScrollPane root = null;
public HashMap<Integer,Boolean> cellEditable = new HashMap<Integer,Boolean>();
private int _row = 0;
private int _col = 0;

// ***************************************************
// コンストラクタ
// ***************************************************
public LboxTable () {
    super(0,0);

    // テーブル作成と同時にスクロールを実装する
    this.root = new JScrollPane(this);

    // 必要な時にスクロールバーを表示する
    this.root.setVerticalScrollBarPolicy(
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    this.root.setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    
}


// ***************************************************
// 確実な初期編集フォーカス
// ***************************************************
public void setCellFocus( int row, String col ){
    setCellFocus( row, GetColumnNo( col ) );
}
public void setCellFocus( int row, int col ){

    this.setRowSelectionInterval(row, row);
    this.setColumnSelectionInterval(col, col);

    Timer t = new Timer();
    _row = row;
    _col = col;
    t.schedule(new TimerTask()
        {
            public void run(){
            if (LboxTable.this.editCellAt(_row,_col) ) {
                DefaultCellEditor ce = 
                    (DefaultCellEditor)LboxTable.this.getCellEditor(_row,_col);
                ce.getComponent().requestFocusInWindow();
                if ( (ce.toString()).indexOf("MyCell") != -1 ) {
                    ((MyCellEditor)ce)._field.selectAll();
                }
            }
            }
        }
    ,500);
}


// ***************************************************
// TAB キーで次のセルに移動(編集可能の場合は編集状態)
// ***************************************************
public void valueChanged( ListSelectionEvent e ){
    int nRow = this.getSelectedRow();
    int nCol = this.getSelectedColumn();
    if ( (nRow != -1) && (nCol != -1) ){
        // System.out.println(this.getCellEditor(nRow,nCol).toString());
        if ( (this.getCellEditor(nRow,nCol).toString()).indexOf("MyCell") != -1 ) {
            this.editCellAt( nRow, nCol );
            ((MyCellEditor)this.getCellEditor(nRow,nCol))._field.selectAll();
        }
    }
    super.valueChanged( e );
}
public void columnSelectionChanged( ListSelectionEvent e ){
    int nRow = this.getSelectedRow();
    int nCol = this.getSelectedColumn();
    if ( (nRow != -1) && (nCol != -1) ){
        // System.out.println(this.getCellEditor(nRow,nCol).toString());
        if ( (this.getCellEditor(nRow,nCol).toString()).indexOf("MyCell") != -1 ) {
            this.editCellAt( nRow, nCol );
            ((MyCellEditor)this.getCellEditor(nRow,nCol))._field.selectAll();
        }
    }
    super.columnSelectionChanged( e );
}


// ***************************************************
// カラムの編集可/不可の設定
// ***************************************************
public void setCellEnable( int column, boolean flg ) {
    cellEditable.put( column, flg );
}
public void setCellEnable( String name, boolean flg ) {
    cellEditable.put( (this.getColumn(name)).getModelIndex(), flg );
}
public boolean isCellEditable(int row, int column) {
    return cellEditable.get( column );
}

// ***************************************************
// カラム追加
// ***************************************************
public void AddColumn(String name) {

    // まず JTable が直接管理しているカラムを作る。
    TableColumn col = new TableColumn();
    this.addColumn(col);

    // データモデルにも指定した名前(Identifier)で追加する
    ((DefaultTableModel)this.dataModel).addColumn(name);

    // このカラム番号
    int column = this.getModelColumnCount()-1;
    // 編集可・不可フラグをセット( 内部テーブル )
    cellEditable.put( column, false );

}
public void AddColumn(String name, boolean flg) {

    // まず JTable が直接管理しているカラムを作る。
    TableColumn col = new TableColumn();
    this.addColumn(col);

    // データモデルにも指定した名前(Identifier)で追加する
    ((DefaultTableModel)this.dataModel).addColumn(name);

    // このカラム番号
    int column = this.getModelColumnCount()-1;
    // 編集可・不可フラグをセット( 内部テーブル )
    cellEditable.put( column, flg );

}

// ***************************************************
// チェックボックスカラムの追加
// ***************************************************
public void setCheckBox(String name) {

    int column = (this.getColumn(name)).getModelIndex();
    TableColumn col = this.getColumnModel().getColumn(column);
    JCheckBox checkbox = new JCheckBox();
    col.setCellRenderer(new MyCheckBoxRenderer());
    col.setCellEditor(new MyCheckBoxEditor(checkbox));

}

// ***************************************************
// セル内チェックボックス表示用
// ***************************************************
private class MyCheckBoxRenderer extends JCheckBox implements TableCellRenderer {
    public MyCheckBoxRenderer() {
        super();
    }
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        if (value == null) {
            this.setSelected(false);
            return this;
        }

        Boolean val = (Boolean)value;
        this.setSelected(val.booleanValue());

        return this;
    }
}
// ***************************************************
// セル内チェックボックス入力用
// ***************************************************
private class MyCheckBoxEditor extends DefaultCellEditor {
    public JCheckBox _checkbox = null;
    public MyCheckBoxEditor(JCheckBox checkbox) {
        super(checkbox);
        _checkbox = checkbox;
    }
}

// ***************************************************
// テキストフィールドカラムの追加
// ***************************************************
public void setTextField(String name) {

    int column = (this.getColumn(name)).getModelIndex();
    TableColumn col = this.getColumnModel().getColumn(column);
    JTextField field = new JTextField();
    col.setCellEditor(new MyCellEditor(field));

}
public void setTextField(String name, final ActionCellField acf) {

    int column = (this.getColumn(name)).getModelIndex();
    TableColumn col = this.getColumnModel().getColumn(column);
    JTextField field = new JTextField();
    field.addKeyListener(new KeyAdapter() {
        public void keyPressed(KeyEvent e) {
            acf.param(
            "pressed",
            e,
            LboxTable.this.getEditingRow() ,
            LboxTable.this.getEditingColumn() 
            );
        }
        public void keyReleased(KeyEvent e) {
            acf.param(
            "released",
            e,
            LboxTable.this.getEditingRow() ,
            LboxTable.this.getEditingColumn() 
            );
        }
    });
    col.setCellEditor(new MyCellEditor(field));

}

// ***************************************************
// セル内フィールドイベント用インターフェイス
// ***************************************************
public interface ActionCellField {
    public void param(String type, KeyEvent ka, int row,int column);
}

// ***************************************************
// セル内テキストフィールド入力用
// ***************************************************
private class MyCellEditor extends DefaultCellEditor {
    public JTextField _field = null;
    public MyCellEditor(JTextField field) {
        super(field);
        _field = field;
    }
}

// ***************************************************
// コンボボックスカラムの追加
// ***************************************************
public void setColumnCombo(String name,String[] items) {

    int column = (this.getColumn(name)).getModelIndex();
    TableColumn col = this.getColumnModel().getColumn(column);
    JComboBox combo = new JComboBox(items);
    col.setCellRenderer(new MyComboBoxRenderer(items));
    col.setCellEditor(new MyComboBoxEditor(combo));

}

// ***************************************************
// セル内コンボボックス入力用
// ***************************************************
private class MyComboBoxRenderer extends JComboBox implements TableCellRenderer {
    public MyComboBoxRenderer(String[] items) {
        super(items);
        this.setFont(new java.awt.Font("ＭＳ Ｐゴシック", 0, 12));

    }

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        if (isSelected) {
            setForeground(table.getSelectionForeground());
            super.setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }

        // Select the current value
        setSelectedItem(value);
        return this;
    }
}

// ***************************************************
// セル内コンボボックス表示用
// ***************************************************
private class MyComboBoxEditor extends DefaultCellEditor {
    public JComboBox _combo = null;
    public MyComboBoxEditor(JComboBox combo ) {
        super(combo);
        combo.setFont(new java.awt.Font("ＭＳ Ｐゴシック", 0, 12));
        _combo = combo;
    }
}


// ***************************************************
// ボタンセルカラムの追加
// ***************************************************
public void setColumnButton(String name,ActionCellButton l) {

    int column = (this.getColumn(name)).getModelIndex();

    TableColumn col = this.getColumnModel().getColumn(column);
    col.setCellRenderer(new MyButtonRenderer());
    col.setCellEditor(new MyButtonEditor(new JCheckBox(),l));

}

// ***************************************************
// セル内ボタンイベント用インターフェイス
// ***************************************************
public interface ActionCellButton {
    public void param(int row,int column);
}

// ***************************************************
// セル内ボタン入力用
// ***************************************************
private class MyButtonEditor extends DefaultCellEditor {
    private JButton button;
    private String label;
    private boolean   isPushed;
    private int _row = 0;
    private int _column = 0;
    private ActionCellButton actionCellButton = null; 
    public MyButtonEditor(JCheckBox checkBox,ActionCellButton l) {
        // チェックボックスで代替
        super(checkBox);
        // インターフェイス
        actionCellButton = l;
        // 処理用のボタンを保持
        button = new JButton();
        button.setFont(new java.awt.Font("ＭＳ Ｐゴシック", 0, 12));
        // ボタンのイベントを引数から構築
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            // デフォルトの処理を中止
            MyButtonEditor.super.fireEditingStopped();
            // 外側の処理を呼び出し
            actionCellButton.param(_row,_column);
            }
        });
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        // 値を文字列として取得
        label = (value == null) ? "" : value.toString();
        // ボタンにセット
        button.setText( label );
        // 行と列の情報を保存
        _row = row;
        _column = column;

        // 仕様どおりに戻す
        return button;
    }
    public Object getCellEditorValue() {
        // 仕様どおりに戻す
        return new String( label );
    }
}

// ***************************************************
// セル内ボタン表示用
// ***************************************************
private class MyButtonRenderer extends JButton implements TableCellRenderer {
    public MyButtonRenderer() {
        super();
        this.setFont(new java.awt.Font("ＭＳ Ｐゴシック", 0, 12));
    }
    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {

        this.setText((value == null) ? "" : value.toString());
        return this;
    }
}

// ***************************************************
// 初期化
// ***************************************************
public void Reset() {
    
    this.Clear();
    
    int cols = this.getColumnCount();

    for( int i = cols-1; i >= 0; i-- ) {
        this.removeColumn((this.getColumnModel()).getColumn(i));
    }

    // データモデルも初期化
    ((DefaultTableModel)this.dataModel).setColumnCount(0);

}

// ***************************************************
// データモデル列数取得
// ***************************************************
public int getModelColumnCount() {
    
    // データモデルより取得
    return ((DefaultTableModel)this.dataModel).getColumnCount();

}
// ***************************************************
// データモデル行数取得
// ***************************************************
public int getModelRowCount() {
    
    // データモデルより取得
    return ((DefaultTableModel)this.dataModel).getRowCount();

}

// ***************************************************
// 行追加
// ***************************************************
public int AddRow( ) {

    // 追加は空で良い
    Object obj [] = null;

    // データモデルに追加
    ((DefaultTableModel)this.dataModel).addRow(obj);
    
    // 追加された行番号を返す
    return this.getModelRowCount()-1;

}

// ***************************************************
// 行を全て削除
// ***************************************************
public void Clear( ) {

    ((DefaultTableModel)this.dataModel).setRowCount(0);

}

// ***************************************************
// カラム値の取得
// ***************************************************
public Object GetColumnText( int nRow, String name ) {

    // 行位置、カラム位置で変更
    this.editCellAt( nRow, -1 );  // 入力の確定に必要
    return this.getValueAt(
        nRow,
        // カラム位置を ID から取得
        (this.getColumn(name)).getModelIndex()
    );

}
// ***************************************************
// カラム番号の取得
// ***************************************************
public int GetColumnNo( String name ) {

    // カラム位置を ID から取得
    return (this.getColumn(name)).getModelIndex();

}
// ***************************************************
// カラム値の変更
// ***************************************************
public void SetColumnText( int nRow, String name, Object value ) {

    // 行位置、カラム位置で変更
    this.setValueAt(
        value,
        nRow,
        // カラム位置を ID から取得
        (this.getColumn(name)).getModelIndex()
    );

}

// ***************************************************
// カラム幅の変更
// ***************************************************
public void SetColumnWidth( String name, int width ) {

    // 自動調整だと、変更しても反映されないので
    this.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );

    TableColumn tc = this.getColumn(name);
    tc.setPreferredWidth(width);

}

// ***************************************************
// カラムタイトルの変更
// ***************************************************
public void SetColumnTitle( String name, String value ) {

    TableColumn tc = this.getColumn(name);
    // ヘッダをまず最初に変更
    tc.setHeaderValue(value);
    // ID が変更されてしまうので戻す
    tc.setIdentifier(name);

    // 再表示が必要
    this.tableHeader.resizeAndRepaint();

}
}
