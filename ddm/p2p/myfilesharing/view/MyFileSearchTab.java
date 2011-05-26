/*
 * FileSearchPanel.java
 *
 * Created on __DATE__, __TIME__
 */

package ddm.p2p.myfilesharing.view;

import java.io.File;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import net.jxta.peergroup.PeerGroup;
import net.jxta.share.ContentAdvertisement;
import ddm.p2p.myfilesharing.download.DownLoadXMLHelper;
import ddm.p2p.myfilesharing.download.FileDownloader;
import ddm.p2p.myfilesharing.search.FileSearcher;

/**
 *
 * @author  __USER__
 */
public class MyFileSearchTab extends javax.swing.JPanel {

	private PeerGroup pg = null;
	private JTextArea txtLog = null;
	private int searchTableRow = -1;
	private String searchKey = null;
	private String originalMD5 = null;
	private String myFileName = null;
	private P2PForm p2pForm = null;

	public MyFileSearchTab(PeerGroup pg, JTextArea txtLog, P2PForm p2pForm,String searchKey) {
		this.pg = pg;
		this.txtLog = txtLog;
		this.p2pForm = p2pForm;
		initComponents();
		this.setOpaque(false);
		this.searchKey=searchKey;
	}

	public JTable getSearchTable() {
		return mySearchTable;
	}
	
	public String getSearchKey(){
		return searchKey;
	}
	/** Creates new form FileSearchPanel */
	public MyFileSearchTab() {
	}

	//GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		searchPop = new javax.swing.JPopupMenu();
		searchDownloadItem = new javax.swing.JMenuItem();
		jLabel1 = new javax.swing.JLabel();
		txtFilename = new javax.swing.JTextField();
		jScrollPane2 = new javax.swing.JScrollPane();
		mySearchTable = new javax.swing.JTable();
		searchBtn = new javax.swing.JButton();

		searchDownloadItem
				.setIcon(new javax.swing.ImageIcon(
						"C:\\Documents and Settings\\Administrator\\Workspaces\\MyEclipse 8.5\\DDM\\icons\\右键搜索下载.png")); // NOI18N
		searchDownloadItem.setText("\u4e0b\u8f7d");
		searchDownloadItem
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						searchDownloadItemActionPerformed(evt);
					}
				});
		searchPop.add(searchDownloadItem);

		jLabel1.setFont(new java.awt.Font("黑体", 1, 14));
		jLabel1.setForeground(new java.awt.Color(0, 153, 0));
		jLabel1.setText("\u6587\u4ef6\u540d");

		txtFilename.setToolTipText("Enter File Name to search for.");

		mySearchTable.setModel(new javax.swing.table.DefaultTableModel(
				new Object[][] { { null, null, null, null, null },
						{ null, null, null, null, null },
						{ null, null, null, null, null },
						{ null, null, null, null, null },
						{ null, null, null, null, null },
						{ null, null, null, null, null },
						{ null, null, null, null, null },
						{ null, null, null, null, null },
						{ null, null, null, null, null },
						{ null, null, null, null, null },
						{ null, null, null, null, null },
						{ null, null, null, null, null },
						{ null, null, null, null, null },
						{ null, null, null, null, null },
						{ null, null, null, null, null },
						{ null, null, null, null, null },
						{ null, null, null, null, null },
						{ null, null, null, null, null },
						{ null, null, null, null, null },
						{ null, null, null, null, null } }, new String[] {
						"文件名", "文件大小(Byte)", "MD5", "资源数", "状态" }) {
			boolean[] canEdit = new boolean[] { false, false, false, false,
					false };

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return canEdit[columnIndex];
			}
		});
		mySearchTable
				.setToolTipText("\u641c\u7d22\u7684\u6587\u4ef6\u5217\u8868");
		mySearchTable.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				mySearchTableMousePressed(evt);
			}
		});
		jScrollPane2.setViewportView(mySearchTable);

		searchBtn
				.setIcon(new javax.swing.ImageIcon(
						"C:\\Documents and Settings\\Administrator\\桌面\\icons\\搜索21.png")); // NOI18N
		searchBtn.setText("\u641c\u7d22");
		searchBtn.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				searchBtnActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout
				.setHorizontalGroup(layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																jScrollPane2,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																598,
																Short.MAX_VALUE)
														.addGroup(
																layout
																		.createSequentialGroup()
																		.addComponent(
																				jLabel1)
																		.addGap(
																				18,
																				18,
																				18)
																		.addComponent(
																				txtFilename,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				328,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																		.addComponent(
																				searchBtn,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				94,
																				javax.swing.GroupLayout.PREFERRED_SIZE)))
										.addContainerGap()));
		layout
				.setVerticalGroup(layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																jLabel1,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																18,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																txtFilename,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																30,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																searchBtn,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																30,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(
												jScrollPane2,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												344,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));
	}// </editor-fold>
	//GEN-END:initComponents

	private void searchDownloadItemActionPerformed(
			java.awt.event.ActionEvent evt) {
		System.out.println("开始下载");
		if (P2PForm.filesearcherThreads.containsKey(searchKey)) {
			FileSearcher fileSearcher = (FileSearcher) P2PForm.filesearcherThreads
					.get(this.searchKey);
			
			P2PForm.advsMap = fileSearcher.getAdvsMap();
			if (searchTableRow != -1) {
				if (P2PForm.advsMap != null) {
					originalMD5 = this.mySearchTable.getValueAt(searchTableRow,
							2).toString();
					myFileName = mySearchTable.getValueAt(searchTableRow, 0)
							.toString();
					
					System.out.println("Info"+originalMD5+myFileName);
					File saveTo = new File(P2PForm.sharedpath + File.separator
							+ myFileName);
					FileDownloader download = null;
					System.out.println("搜索关键字为"+searchKey+"含有广告");
					if (!P2PForm.downloadThreads.containsKey(originalMD5)) {//如果不存在文件下载线程，需要新建该下载线程
						download = new FileDownloader(pg, P2PForm.advsMap.get(
								originalMD5).toArray(
								new ContentAdvertisement[0]), saveTo,
								this.txtLog, -1, this.p2pForm
										.getDownloadTable(),
								P2PForm.unloadedFilesReader,
								P2PForm.loadedFilesReader);
						P2PForm.downloadThreads.put(originalMD5, download);//放入下载线程池 
						DownLoadXMLHelper xmlHelper = DownLoadXMLHelper
								.getInstance();
						xmlHelper.addFileInfo(myFileName, originalMD5,
								P2PForm.advsMap.get(originalMD5).get(0)
										.getLength()
										+ "");
						P2PForm.unloadedFilesReader.readFilesFromConfig();
						P2PForm.downloadThreadsExecutor.execute(download);
						new MyDownloadInfoPop("下载提示","新增下载:" + myFileName);
					}
				}

			} else {
				JOptionPane.showMessageDialog(null, "请选择一个文件下载进行下载！！", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void searchBtnActionPerformed(java.awt.event.ActionEvent evt) {
		//Start Searching for contents in Peer Group
		if ((txtFilename.getText()!="")&&(!P2PForm.filesearcherThreads
				.containsKey(this.txtFilename.getText()))) {
			MyFileSearchTab searchPanel = new MyFileSearchTab(this.pg,
					this.txtLog, this.p2pForm,this.txtFilename.getText());
			p2pForm.getSearchTabPanel().addTab("搜索:" + this.txtFilename.getText(),
					searchPanel);
			P2PForm.filesearcherPanels.put(this.txtFilename.getText(),
					searchPanel);
			FileSearcher fileSearcher = new FileSearcher(pg, this.txtFilename
					.getText(), this.txtLog, searchPanel.getSearchTable());
			fileSearcher.start();
			P2PForm.filesearcherThreads.put(this.txtFilename.getText(),
					fileSearcher);
		}
	}

	private void mySearchTableMousePressed(java.awt.event.MouseEvent evt) {
		// TODO add your handling code here:
		if (SwingUtilities.isRightMouseButton(evt)) {
			searchTableRow = mySearchTable.rowAtPoint(evt.getPoint());
			mySearchTable.clearSelection();
			mySearchTable.addRowSelectionInterval(searchTableRow,
					searchTableRow);

			System.out.println("searchTableRow:" + searchTableRow);
			if (mySearchTable.getValueAt(searchTableRow, 4) == null
					|| mySearchTable.getValueAt(searchTableRow, 4)
							.equals("已下载")) {
				this.searchDownloadItem.setEnabled(false);
				System.out.println("已下载");
			} else {
				this.searchDownloadItem.setEnabled(true);
			}
			this.searchPop.show(evt.getComponent(), evt.getX(), evt.getY());
		}
	}

	//GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JLabel jLabel1;
	private javax.swing.JScrollPane jScrollPane2;
	public javax.swing.JTable mySearchTable;
	private javax.swing.JButton searchBtn;
	private javax.swing.JMenuItem searchDownloadItem;
	private javax.swing.JPopupMenu searchPop;
	private javax.swing.JTextField txtFilename;
	// End of variables declaration//GEN-END:variables

}