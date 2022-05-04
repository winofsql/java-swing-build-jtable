import javax.swing.*;
import java.awt.*;
import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import lightbox.*;
import java.awt.event.*;

public class Main extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel jContentPane = null;
    private JPanel jPanel = null;
    private LboxTable lboxTable = null;
    /**
     * This method initializes jPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJPanel() {
        if (jPanel == null) {
            jPanel = new JPanel();
            jPanel.setLayout(new BoxLayout(getJPanel(), BoxLayout.X_AXIS));
            jPanel.setBounds(new Rectangle(11, 67, 850, 500));
            jPanel.add(getLboxTable(), null);
        }
        return jPanel;
    }

    /**
     * This method initializes lboxTable	
     * 	
     * @return lightbox.LboxTable	
     */
    private JScrollPane getLboxTable() {
        if (lboxTable == null) {
            lboxTable = new LboxTable();

            // *********************************************
            // JTable のイベント( ダブルクリック用 )
            // *********************************************
            lboxTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // ダブルクリックの処理
                if ( e.getClickCount() == 2 ) {
                    int row = lboxTable.convertRowIndexToModel(
                lboxTable.rowAtPoint(e.getPoint())
                    );
                    int col = lboxTable.convertColumnIndexToModel(
                lboxTable.columnAtPoint(e.getPoint())
                    );
        
                    // 列のテキストを取得
                    String col_1 = (String)lboxTable.GetColumnText( row, "COLUMN_1" );
                    String col_2 = (String)lboxTable.GetColumnText( row, "COLUMN_2" );
                    String col_3 = (String)lboxTable.GetColumnText( row, "COLUMN_3" );
                    String col_4 = (String)lboxTable.GetColumnText( row, "COLUMN_4" );
                    String col_5 = (String)lboxTable.GetColumnText( row, "COLUMN_5" );
                    String col_6 = (String)lboxTable.GetColumnText( row, "COLUMN_6" );
                    String col_7 = (lboxTable.GetColumnText( row, "COLUMN_7" )).toString();
                    System.out.println("dbl:"+row+"/"+col);
                    System.out.println(col_1+";"+col_2+";"+col_3+
                ";"+col_4+";"+col_5+";"+col_6+";"+col_7);
        
                }
            }
            });

            lboxTable.setRowHeight( 20 );

            // *********************************************
            // 列追加( 最初に全て追加しておく必要あり )
            // *********************************************
            lboxTable.AddColumn("COLUMN_1", true );
            lboxTable.AddColumn("COLUMN_2", false );
            lboxTable.AddColumn("COLUMN_3", true );
            lboxTable.AddColumn("COLUMN_4", true );
            lboxTable.AddColumn("COLUMN_5", true );
            lboxTable.AddColumn("COLUMN_6", true );
            lboxTable.AddColumn("COLUMN_7", true );
            // *********************************************
            // 列追加( 最初に全て追加しておく必要あり )
            // *********************************************


            // 1番目のフィールドのイベント( KeyEvent )
            lboxTable.setTextField("COLUMN_1",new LboxTable.ActionCellField() {
                // ユーザ定義イベント用インターフェイス( ActionCellField )
                public void param(String type, KeyEvent e, int row,int column) {
                    // type : "pressed" または "released"
                    // 通常 "pressed" で良いです
                    System.out.println(type+"|"+e.getKeyCode()+"|"+row+"|"+column);

                    // Enter キーでフォーカス移動
                    if ( type.equals( "pressed" ) && e.getKeyCode() == 10 ) {
                        lboxTable.setCellFocus(row, "COLUMN_6" );
                    }
                }
            });
            // コンボボックス
            lboxTable.setColumnCombo("COLUMN_3", new String[]{"あ", "い", "う"});

            // ボタン
            lboxTable.setColumnButton("COLUMN_4",new LboxTable.ActionCellButton() {
                // ユーザ定義イベント用インターフェイス( ActionCellButton )
                public void param(int row,int column) {
                    System.out.println("btn:"+row+"/"+column);
                }
            });

            // コンボボックス
            lboxTable.setColumnCombo("COLUMN_5", new String[]{"A", "B", "C"});

            // イベント処理をしないテキストフィールド
            lboxTable.setTextField("COLUMN_6");

            // チェックボックス
            lboxTable.setCheckBox("COLUMN_7");


            // *********************************************
            // タイトル
            // *********************************************
            lboxTable.SetColumnTitle("COLUMN_1", "タイトル");
            lboxTable.SetColumnTitle("COLUMN_2", "URL");
            lboxTable.SetColumnTitle("COLUMN_3", "コンボ1");
            lboxTable.SetColumnTitle("COLUMN_4", "ボタン");
            lboxTable.SetColumnTitle("COLUMN_5", "コンボ2");
            lboxTable.SetColumnTitle("COLUMN_6", "テキスト");

            // *********************************************
            // 幅
            // *********************************************
            lboxTable.SetColumnWidth("COLUMN_1", 150);
            lboxTable.SetColumnWidth("COLUMN_2", 320);
            lboxTable.SetColumnWidth("COLUMN_3", 60);
            lboxTable.SetColumnWidth("COLUMN_4", 100);
            lboxTable.SetColumnWidth("COLUMN_5", 60);
            lboxTable.SetColumnWidth("COLUMN_6", 50);

            lboxTable.setSurrendersFocusOnKeystroke( true );

            try {
                // 生のバイトのストリーム
                // ( カレントディレクトリの exlink.xml )
                FileInputStream fis = 
                    new FileInputStream(System.getProperty("user.dir")+"\\exlink.xml");

                // XML 取得の準備
                DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = dbfactory.newDocumentBuilder();
                // InputStream から Documentオブジェクトを取得
                Document doc = builder.parse(fis);

                // ここから先は一般的な DOM の処理
                // ルート要素
                Element root = doc.getDocumentElement();
                // System.out.println(root.getNodeName());
                
                // <row> ～ </row>
                // getElementsByTagName が一番直感的で確実
                NodeList nl1 = root.getElementsByTagName("row");
                // 一つ目のノード
                Node nd1 = nl1.item(0);
                // System.out.println(nd1.getNodeName());
                
                // <fld3>名称</fld3>
                // Node を Element にキャストして getElementsByTagName を使う
                NodeList nl2 = ((Element)nd1).getElementsByTagName("fld3");
                NodeList nl3 = ((Element)nd1).getElementsByTagName("fld2");
                // 最初の row ブロックの fld3 の 列挙
                for( int i = 0; i < nl2.getLength(); i++ ) {

                    // <fld3>
                    // System.out.println((nl2.item(i)).getNodeName());
                    // 要素に挟まれた値は、実際はテキストノードの中にあります
                    int nRow = lboxTable.AddRow();
                    lboxTable.SetColumnText(nRow, "COLUMN_1",
                        (nl2.item(i)).getFirstChild().getNodeValue());
                    lboxTable.SetColumnText(nRow, "COLUMN_2", 
                        (nl3.item(i)).getFirstChild().getNodeValue());
                    lboxTable.SetColumnText(nRow, "COLUMN_3", "あ");
                    lboxTable.SetColumnText(nRow, "COLUMN_4", "ボタン"+i);
                    lboxTable.SetColumnText(nRow, "COLUMN_5", "B");
                    lboxTable.SetColumnText(nRow, "COLUMN_6", "" );
                    lboxTable.SetColumnText(nRow, "COLUMN_7", false );

                    nRow = lboxTable.AddRow();
                    lboxTable.SetColumnText(nRow, "COLUMN_1", 
                        (nl2.item(i)).getFirstChild().getNodeValue());
                    lboxTable.SetColumnText(nRow, "COLUMN_2", 
                        (nl3.item(i)).getFirstChild().getNodeValue());
                    lboxTable.SetColumnText(nRow, "COLUMN_3", "う");
                    lboxTable.SetColumnText(nRow, "COLUMN_4", "ボタン"+i);
                    lboxTable.SetColumnText(nRow, "COLUMN_5", "A");
                    lboxTable.SetColumnText(nRow, "COLUMN_6", "初期値" );
                    lboxTable.SetColumnText(nRow, "COLUMN_7", true );

                    nRow = lboxTable.AddRow();
                    lboxTable.SetColumnText(nRow, "COLUMN_1", "空行" );
                    lboxTable.SetColumnText(nRow, "COLUMN_2", "空行" );
                    lboxTable.SetColumnText(nRow, "COLUMN_3", "い");
                    lboxTable.SetColumnText(nRow, "COLUMN_5", "B");
                    lboxTable.SetColumnText(nRow, "COLUMN_6", "" );
                    lboxTable.SetColumnText(nRow, "COLUMN_7", false );

                    nRow = lboxTable.AddRow();
                    lboxTable.SetColumnText(nRow, "COLUMN_1", "空行" );
                    lboxTable.SetColumnText(nRow, "COLUMN_2", "空行" );
                    lboxTable.SetColumnText(nRow, "COLUMN_3", "い");
                    lboxTable.SetColumnText(nRow, "COLUMN_5", "B");
                    lboxTable.SetColumnText(nRow, "COLUMN_6", "" );
                    lboxTable.SetColumnText(nRow, "COLUMN_7", false );
                }

                // ファイルを閉じる
                fis.close();

                // 状態表示
                System.out.println("列数:"+lboxTable.getColumnCount());
                System.out.println("行数:"+lboxTable.getRowCount());
                System.out.println("列名1:"+lboxTable.getColumnName(0));
                System.out.println("列名2:"+lboxTable.getColumnName(1));

                // 初期編集フォーカス
                lboxTable.setCellFocus(0,0);

            }
            catch (Exception e) {
            }
            
        }
        return lboxTable.root;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Main thisClass = new Main();
                thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                thisClass.setVisible(true);
            }
        });
    }

    /**
     * This is the default constructor
     */
    public Main() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        this.setSize(900, 700);
        this.setContentPane(getJContentPane());
        this.setTitle("JFrame");
    }

    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new JPanel();
            jContentPane.setLayout(null);
            jContentPane.add(getJPanel(), null);
        }
        return jContentPane;
    }

}
