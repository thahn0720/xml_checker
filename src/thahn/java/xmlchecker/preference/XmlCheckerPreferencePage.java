package thahn.java.xmlchecker.preference;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.wb.swt.SWTResourceManager;

import thahn.java.xmlchecker.XmlCheckerPlugin;
import thahn.java.xmlchecker.parser.standard.DescriptorStandard;
import thahn.java.xmlchecker.preference.StandardXmlInfo.StandardXmlInfoBuilder;
import thahn.java.xmlchecker.util.MyUtils;
import thahn.java.xmlchecker.util.MyStrings;
import thahn.java.xmlchecker.view.DialogListener;

/**
 * 1. 검사하는 파일의 Root Tag가 Standard Descriptor의 Root Tag와 다르면 Standard Descriptor에서 Root Tag의 
 * Attribute로 {@link DescriptorStandard#ATTR_IGNORED}가 있다면  검사하는 파일의 Root Tag를 {@link DescriptorStandard#ATTR_ALL_AROUND}
 * 에서 검색하여 있으면 해당 Standard Descriptor를 사용한다.
 * 2. Regular Expression으로 표현된 File Name을 검사하여 맞으면 해당 Standard Descriptor를 사용한다.  
 * <br>
 * <Tag Value> <br>
 * - Tag Value를 그냥 쓰면 Content Assist가 되고 '@'를 prefix로 하여 regular expression을 작성하면 체크가 된다.
 * @author th0720.ahn
 *
 */
public class XmlCheckerPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	
	public static final String 								PREFERENCE_MAIN_ID		= "thahn.java.descriptorchecker.preferences.main";
	
	private static final String[]							COLUMNS 			= new String[] {
																"Standard XML Path", "Root Tag", "File Name"
															};
	
	private static final int								INDEX_STD_XML_PATH 	= 0;
	private static final int								INDEX_ROOT_TAG	 	= 1;
	private static final int								INDEX_FILE_NAME	 	= 2;
	
	private Table 											table;
	private final FormToolkit 								formToolkit 		= new FormToolkit(Display.getDefault());
	private AddStandardXmlInfoDialog 						addStdXmlInfoDialog;
	private int				 								selectedIndex		= -1;
	private List<StandardXmlInfo> 							stdXmlInfoList;
	
	/**
	 * Create the preference page.
	 */
	public XmlCheckerPreferencePage() {
	}

	/**
	 * Create contents of the preference page.
	 * @param parent
	 */
	@Override
	public Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout gl_container = new GridLayout(2, false);
		container.setLayout(gl_container);
		
		table = new Table(container, SWT.BORDER | SWT.FULL_SELECTION);
		GridData gd_table = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_table.widthHint = 240;
		table.setLayoutData(gd_table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		stdXmlInfoList = XmlCheckerPrefs.getPrefs().getStdXmlInfos();
		refreshTable(false);
		
		Composite composite = new Composite(container, SWT.NONE);
		composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		formToolkit.adapt(composite);
		formToolkit.paintBordersFor(composite);
		
		Button btnModify = formToolkit.createButton(composite, "Modify", SWT.NONE);
		btnModify.setBounds(0, 31, 89, 25);
		
		Button btnAdd = formToolkit.createButton(composite, "Add", SWT.NONE);
		btnAdd.setBounds(0, 0, 89, 25);
		
		Button btnRemove = formToolkit.createButton(composite, "Remove", SWT.NONE);
		btnRemove.setBounds(0, 62, 89, 25);
		
		Button btnUp = new Button(composite, SWT.NONE);
		btnUp.setBounds(0, 94, 89, 25);
		btnUp.setText("Up");
		
		Button btnDown = new Button(composite, SWT.NONE);
		btnDown.setBounds(0, 125, 89, 25);
		btnDown.setText("Down");
		
		Button btnImport = new Button(composite, SWT.NONE);
		btnImport.setBounds(0, 156, 89, 25);
		btnImport.setText("Import");
		
		Button btnExport = new Button(composite, SWT.NONE);
		btnExport.setBounds(0, 187, 89, 25);
		btnExport.setText("Export");
		
		btnModify.addSelectionListener(modifyButtonSelectionListener);
		btnAdd.addSelectionListener(addButtonSelectionListener);
		btnRemove.addSelectionListener(removeButtonSelectionListener);
		btnUp.addSelectionListener(upButtonSelectionListener);
		btnDown.addSelectionListener(downButtonSelectionListener);
		btnImport.addSelectionListener(importButtonSelectionListener);
		btnExport.addSelectionListener(exportButtonSelectionListener);
		
		return container;
	}
	
	private void refreshTable() {
		refreshTable(true);
	}
	
	private void refreshTable(boolean isSave) {
		table.removeAll();
		
		for (int i = 0; i < COLUMNS.length; i++) {
			TableColumn column = new TableColumn(table, SWT.NULL);
			column.setText(COLUMNS[i]);
		}

		for (StandardXmlInfo info : stdXmlInfoList) {
			TableItem item = new TableItem(table, SWT.NULL);
			item.setText(INDEX_STD_XML_PATH, info.getStandardXmlPath());
			item.setText(INDEX_ROOT_TAG, StandardXmlInfo.joinRootTagString(info.getRootTag()));
			item.setText(INDEX_FILE_NAME, info.getCondition().getFileName());
		}

		for (int i = 0; i < COLUMNS.length; i++) {
			table.getColumn(i).pack();
		}
		
		if (isSave) {
			XmlCheckerPrefs.getPrefs().setStdXmlInfos(stdXmlInfoList);
		}
	}
	
	/**
	 * Initialize the preference page.
	 */
	public void init(IWorkbench workbench) {
	}
	
	private SelectionListener addButtonSelectionListener = new SelectionListener() {
		
		@Override
		public void widgetSelected(SelectionEvent e) {
			if (addStdXmlInfoDialog == null) {
				addStdXmlInfoDialog = new AddStandardXmlInfoDialog(XmlCheckerPlugin.getDisplay().getActiveShell());
				addStdXmlInfoDialog.setDialogListener(dialogListener);
			}
			addStdXmlInfoDialog.setBlockOnOpen(true);
			int ret = addStdXmlInfoDialog.open();
			if (ret == Dialog.OK) {
			} else if (ret == Dialog.CANCEL) {
			}			
		}
		
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}
	};
	
	private SelectionListener modifyButtonSelectionListener = new SelectionListener() {
		
		@Override
		public void widgetSelected(SelectionEvent e) {
			int[] indices = table.getSelectionIndices();
			if (indices.length == 1) {
				selectedIndex = indices[0];
				if (addStdXmlInfoDialog == null) {
					addStdXmlInfoDialog = new AddStandardXmlInfoDialog(XmlCheckerPlugin.getDisplay().getActiveShell());
					addStdXmlInfoDialog.setDialogListener(dialogListener);
				}
				addStdXmlInfoDialog.setBlockOnOpen(false);
				addStdXmlInfoDialog.open();
				addStdXmlInfoDialog.setDefaultValue(stdXmlInfoList.get(indices[0]));
			} else {
				XmlCheckerPlugin.displayError("Modify Standard XML Information", "choose a one item.");
			}
		}
		
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}
	};
	
	private SelectionListener removeButtonSelectionListener = new SelectionListener() {
		
		@Override
		public void widgetSelected(SelectionEvent e) {
			for (int i : table.getSelectionIndices()) {
				stdXmlInfoList.remove(i);
			}
			refreshTable();
		}
		
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}
	};
	
	private SelectionListener upButtonSelectionListener = new SelectionListener() {
		
		@Override
		public void widgetSelected(SelectionEvent e) {
			moveTo(true);
		}
		
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}
	};
	
	private SelectionListener downButtonSelectionListener = new SelectionListener() {
		
		@Override
		public void widgetSelected(SelectionEvent e) {
			moveTo(false);
		}
		
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}
	};
	
	private SelectionListener importButtonSelectionListener = new SelectionListener() {
		
		@Override
		public void widgetSelected(SelectionEvent e) {
			FileDialog dialog = new FileDialog(getShell(), SWT.OPEN | SWT.SHEET);
	        String file = dialog.open();
	        if (file != null) {
	        	try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(file)))) {
	        		String value = MyStrings.getText(bis);
	        		stdXmlInfoList = XmlCheckerPrefs.getPrefs().getStdXmlInfosObject(value);
	        		refreshTable(true);
	        	} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
	        }
		}
		
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}
	};
	
	private SelectionListener exportButtonSelectionListener = new SelectionListener() {
		
		@Override
		public void widgetSelected(SelectionEvent e) {
			FileDialog dialog = new FileDialog(getShell(), SWT.OPEN | SWT.SHEET);
	        String file = dialog.open();
	        if (file != null) {
	        	try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(file)))) {
	        		String value = XmlCheckerPrefs.getPrefs().getStdXmlInfosJson(stdXmlInfoList);
	        		bos.write(value.getBytes());
	        	} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
	        }
		}
		
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}
	};
	
	private void moveTo(boolean isUp) {
		boolean isChanged = false;
		int[] selected = table.getSelectionIndices();
		Arrays.sort(selected);
		if (!isUp) {
			int[] temp = new int[selected.length];
			for (int i = 0; i < temp.length; i++) {
				temp[i] = selected[temp.length - i - 1];
			}
		}
		
		for (int i = 0; i < selected.length; i++) {
			if (isUp) { // up
				if (selected[i] == 0) {
					break;
				}
				isChanged = true;
				StandardXmlInfo info = stdXmlInfoList.get(selected[i]);
				stdXmlInfoList.remove(selected[i]);
				selected[i] -= 1;
				stdXmlInfoList.add(selected[i], info);
			} else if (!isUp) { // down
				if (selected[i] == stdXmlInfoList.size()-1) {
					break;
				}
				isChanged = true;
				StandardXmlInfo info = stdXmlInfoList.get(selected[i]);
				stdXmlInfoList.remove(selected[i]);
				selected[i] += 1;
				stdXmlInfoList.add(selected[i], info);
			}
		}
		
		if (isChanged) {
			refreshTable();
			table.setSelection(selected);
		}
	}
	
	private DialogListener dialogListener = new DialogListener() {
		
		@Override
		public void ok(Dialog dialog) {
			StandardXmlInfoBuilder builder = StandardXmlInfo.builder()
					.standardXmlPath(addStdXmlInfoDialog.getStdXmlPathText())
					.rootTag(StandardXmlInfo.splitRootTagString(addStdXmlInfoDialog.getRootTagText()))
					.standardCondition(StandardCondition.builder()
							.fileName(addStdXmlInfoDialog.getFileNameText()).build());
			
			if (selectedIndex != -1) { // update
				stdXmlInfoList.remove(selectedIndex);
				stdXmlInfoList.add(selectedIndex, builder.build());
				selectedIndex = -1;
			} else { // add 
				stdXmlInfoList.add(builder.build());
			}
			refreshTable();
		}
		
		@Override
		public void cancel(Dialog dialog) {
		}
	};

	@Override
	public void applyData(Object data) {
		super.applyData(data);
	}
}
