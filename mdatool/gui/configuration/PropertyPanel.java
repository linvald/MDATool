package mdatool.gui.configuration;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

/**
 * @author Jesper Linvald (jesper@linvald.net)
 *
 */
public class PropertyPanel extends JPanel  implements TableModelListener {
	
	
	protected JTable _table;
	protected JScrollPane _scroll;
	
	public PropertyPanel(Object[][] data){
		_table = new JTable(new PropertyTableModel(data));
		initTable();
		initGUI();
	}
	
	private void initTable(){
		_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_table.getModel().addTableModelListener(this);
	}
	
	private void initGUI(){
		_scroll = new JScrollPane(_table);
		setLayout(new BorderLayout());
		add(_scroll);

	}
	
	public Object getValueByKey(String key){
		return ((PropertyTableModel)_table.getModel()).getValueByKey(key);
	}
	public void setValueByKey(String key, Object value){
		((PropertyTableModel)_table.getModel()).setValue(key,value);
	}
	public JTable getTable(){
		return this._table;
	}
	
	
	/* (non-Javadoc)
	 * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
	 */
	public void tableChanged(TableModelEvent e) {
		//TODO: EVENT FIRERING
		int row = e.getFirstRow();
		int column = e.getColumn();
		PropertyTableModel model = (PropertyTableModel) e.getSource();
		Object val = (Object)model.getValueByKey("Canvas color");

		System.out.println("Found val by key:" + val.getClass());
		String columnName = model.getColumnName(column);
		Object data = model.getValueAt(row, column);
		System.out.println("Row:" + row + " column:" + column + " model:" + model + " Columnname:" + columnName +
		" data" + data);
		System.out.println("Field name:" + model.getValueAt(row,0));
	}
	
	
	class PropertyTableModel extends AbstractTableModel {
		private Object[][] data;
		private String[] columnNames = {"Property","Value"};	
		
											
		public PropertyTableModel(Object[][] data){
			this.data = data;
		
		}
		
		
		
			
			public int getColumnCount() {
				return columnNames.length;
			}

			public int getRowCount() {
				return data.length;
			}

			public String getColumnName(int col) {
				//return columnNames[col];
				if(col == 0){
					return "Property";
				}else if(col == 1){
					return "Value";
				}else return ""; 
			}


		public Object getValueAt(int row, int col) {
			return data[row][col];
		}
		public Object getValueByKey(String key){
			for (int i = 0; i < data.length; i++) {
				String rowVal = (String)data[i][0];
				if(key.equals(rowVal)){
					return data[i][1]; 
				}
			}
			return null;
		}
			
		public void setValue(String key, Object val){
		
			for (int i = 0; i < data.length; i++) {
				String rowVal = (String)data[i][0];
				System.out.println("row:" + rowVal);
				if(key.equals(rowVal)){
					data[i][1] = val; 
				}
			}
		}

			/*
			 * JTable uses this method to determine the default renderer/
			 * editor for each cell.  If we didn't implement this method,
			 * then the last column would contain text ("true"/"false"),
			 * rather than a check box.
			 */
			public Class getColumnClass(int c) {
				/*if(getValueAt(0, c)!=null)
					return getValueAt(0, c).getClass();
				else return Object.class;*/
				return getValueAt(0, c).getClass();	
			}



			/*
			 * Don't need to implement this method unless your table's
			 * editable.
			 */
			public boolean isCellEditable(int row, int col) {
				//Note that the data/cell address is constant,
				//no matter where the cell appears onscreen.
				if (col == 1) {
					return true;
				} else {
					return false;
				}
			}

			/*
			 * Don't need to implement this method unless your table's
			 * data can change.
			 */
			public void setValueAt(Object value, int row, int col) {
				data[row][col] = value;
				fireTableCellUpdated(row, col);
			}
		
		}

	public static void main(String[] args) {
		JFrame f = new JFrame("Table test");
		Object[][] data = {
						//	{"Canvas color",Color.red },
						//	{"Line width",Color.red},
			{"Line width",new Boolean(true)}
						};
		PropertyPanel p = new PropertyPanel(data);
	//	p.getTable().getColumnModel().getColumn(1).setCellEditor(new ColorEditor());	
	//	p.getTable().getColumnModel().getColumn(1).setCellRenderer(new ColorRenderer(true));
		//p.getTable().getColumnModel().getColumn(1).		
		p.setValueByKey("Canvas color",Color.green);
		System.out.println("COLOR IS NOW:"+ p.getValueByKey("Canvas color"));			
		f.getContentPane().add(p);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack();
		f.setVisible(true);
	}


}
