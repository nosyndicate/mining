package ddm.p2p.myfilesharing.view;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.jvnet.substance.skin.*;

import net.jxta.peergroup.PeerGroup; ///import net.jxta.platform.NetworkManager;
import net.jxta.share.ContentAdvertisement;
import net.jxta.socket.JxtaMulticastSocket;

import ddm.p2p.myfilesharing.chat.model.OnlineMessage;
import ddm.p2p.myfilesharing.chat.service.JxtaMulticastSocketService;
import ddm.p2p.myfilesharing.chat.service.JxtaServerSocketService;
import ddm.p2p.myfilesharing.chat.service.Service;
import ddm.p2p.myfilesharing.chat.threads.CreateChatsessionThread;
import ddm.p2p.myfilesharing.chat.util.XmlSender;
import ddm.p2p.myfilesharing.ddm.DDMService;
import ddm.p2p.myfilesharing.download.DownLoadXMLHelper;
import ddm.p2p.myfilesharing.download.FileDownloadInfoReader;
import ddm.p2p.myfilesharing.download.FileDownloader;
import ddm.p2p.myfilesharing.download.FileLoadedInfoReader;
import ddm.p2p.myfilesharing.download.FileUnloadedInfoReader;
import ddm.p2p.myfilesharing.jxtaservice.JXTAStarter;
import ddm.p2p.myfilesharing.search.FileContSearcher;
import ddm.p2p.myfilesharing.search.FileSearcher;
import ddm.p2p.myfilesharing.settings.SettinDialog;
import ddm.p2p.myfilesharing.share.FileSharedInfoReader;
import ddm.p2p.myfilesharing.share.FileSharer;
import ddm.p2p.myfilesharing.share.ShareXMLHelper;
import ddm.p2p.myfilesharing.utils.ConfigCheck;
import ddm.p2p.myfilesharing.utils.FileHelper;
import ddm.p2p.myfilesharing.utils.FirsttimeChecker;
import ddm.p2p.myfilesharing.utils.ThreadPoolUtil;

public class P2PForm extends javax.swing.JFrame {

	public static String sharedpath = null;

	//JXTA变量-------------------------------------------------------------------
	static JXTAStarter connection = null;

	//共享模块-------------------------------------------------------------------
	static FileSharer fileSharer = null;
	static public FileSharedInfoReader sharedFilesReader = null;
	private ShareXMLHelper sharexmlHelper = new ShareXMLHelper();

	//搜索模块-------------------------------------------------------------------
	public static Map<String, FileSearcher> filesearcherThreads = new HashMap<String, FileSearcher>();
	public static ConcurrentHashMap<String, MyFileSearchTab> filesearcherPanels = new ConcurrentHashMap<String, MyFileSearchTab>();

	//static FileSearcher fileSearcher = null; //搜索变量
	static public Map<String, ArrayList<ContentAdvertisement>> advsMap = new HashMap<String, ArrayList<ContentAdvertisement>>();

	//下载模块-------------------------------------------------------------------
	static public Map<String, FileDownloader> downloadThreads = new HashMap<String, FileDownloader>(); //下载线程池
	public static ExecutorService downloadThreadsExecutor = Executors
			.newFixedThreadPool(50);
	static public Map<String, FileContSearcher> contsearchThreads = new HashMap<String, FileContSearcher>(); //续接资源线程池	
	public static ExecutorService contsearchThreadsExecutor = Executors
			.newFixedThreadPool(50);
	static public FileDownloadInfoReader loadedFilesReader = null;
	static public FileDownloadInfoReader unloadedFilesReader = null;
	private DownLoadXMLHelper downloadxmlHelper = new DownLoadXMLHelper();

	public JTable getDownloadTable() {
		return myDownloadTable;
	}

	public JTabbedPane getSearchTabPanel() {
		return mySearchTabPanel;
	}

	//聊天模块-------------------------------------------------------------------
	public static PeerGroup pg = null;
	public static Vector<OnlineMessage> peers = new Vector<OnlineMessage>();
	public static ConcurrentHashMap chatwins = new ConcurrentHashMap();
	private Service[] service;
	private ExecutorService serviceExecutor;

	//视图变量-------------------------------------------------------------------
	private static int unloadedTableRow = -1;
	private static int searchTableRow = -1;
	private static int loadedTableRow = -1;
	private static int sharedTableRow = -1;
	private static int recommandRow = -1;
	static String originalMD5 = "unknown";
	static String myFileName = null;
	private FileHelper fileHelper = FileHelper.getInstance();

	//构造函数-------------------------------------------------------------------
	public P2PForm() {
		initComponents();//初始化控件
		//添加关闭按钮事件
		mySearchTabPanel.addCloseListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals(mySearchTabPanel.ON_TAB_CLOSE)) {
					if (mySearchTabPanel.getTabCount() != 1) {
						MyFileSearchTab panel = (MyFileSearchTab) mySearchTabPanel
								.getComponentAt(mySearchTabPanel
										.getSelectedIndex());
						P2PForm.filesearcherPanels.remove(panel.getSearchKey());
						P2PForm.filesearcherThreads
								.remove(panel.getSearchKey());
						mySearchTabPanel.removeTabAt(mySearchTabPanel
								.getSelectedIndex());
						System.out.println("close search tab,key value is "
								+ mySearchTabPanel.getSelectedIndex()
								+ panel.getSearchKey());
					}
				}
			}
		});
		//设置弹出菜单
		JPopupMenu menu = new JPopupMenu();
		menu.add(new JMenuItem("关闭"));
		mySearchTabPanel.setPopup(menu);
	}

	//GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		downloadPop = new javax.swing.JPopupMenu();
		connectResItem = new javax.swing.JMenuItem();
		jSeparator3 = new javax.swing.JSeparator();
		disconnectResItem = new javax.swing.JMenuItem();
		jSeparator8 = new javax.swing.JSeparator();
		contDownloadItem = new javax.swing.JMenuItem();
		jSeparator9 = new javax.swing.JSeparator();
		pauseDownloadItem = new javax.swing.JMenuItem();
		jSeparator13 = new javax.swing.JSeparator();
		deleteUnloadedfileItem = new javax.swing.JMenuItem();
		downloadedTablePop = new javax.swing.JPopupMenu();
		openFileItem = new javax.swing.JMenuItem();
		jSeparator19 = new javax.swing.JSeparator();
		deleteItem = new javax.swing.JMenuItem();
		jSeparator20 = new javax.swing.JSeparator();
		openFolderItem = new javax.swing.JMenuItem();
		jSeparator21 = new javax.swing.JSeparator();
		refreshDownloadedFilesItem = new javax.swing.JMenuItem();
		sharedTabelPop = new javax.swing.JPopupMenu();
		sharedOpenFileItem = new javax.swing.JMenuItem();
		jSeparator14 = new javax.swing.JSeparator();
		deletedSharedFileItem = new javax.swing.JMenuItem();
		jSeparator15 = new javax.swing.JSeparator();
		sharedOpenFolderItem = new javax.swing.JMenuItem();
		jSeparator17 = new javax.swing.JSeparator();
		sharedFilesRereshItem = new javax.swing.JMenuItem();
		jSeparator18 = new javax.swing.JSeparator();
		sharedFilesAddItem = new javax.swing.JMenuItem();
		jSeparator7 = new javax.swing.JSeparator();
		unshareFileItem = new javax.swing.JMenuItem();
		trayPop = new javax.swing.JPopupMenu();
		exitItem = new javax.swing.JMenuItem();
		recommandPop = new javax.swing.JPopupMenu();
		recommandItem = new javax.swing.JMenuItem();
		jTabbedPane1 = new javax.swing.JTabbedPane();
		jPanel1 = new javax.swing.JPanel();
		jScrollPane1 = new javax.swing.JScrollPane();
		txtLog = new javax.swing.JTextArea();
		jPanel2 = new javax.swing.JPanel();
		mySearchTabPanel = new ddm.p2p.myfilesharing.view.MySearchTabbedPanel();
		jPanel3 = new javax.swing.JPanel();
		jScrollPane3 = new javax.swing.JScrollPane();
		mySharedTable = new javax.swing.JTable();
		jSeparator4 = new javax.swing.JSeparator();
		addSharedFiles = new javax.swing.JButton();
		btnReload = new javax.swing.JButton();
		jPanel4 = new javax.swing.JPanel();
		downloadTabbedPanel = new javax.swing.JTabbedPane();
		jPanel8 = new javax.swing.JPanel();
		jScrollPane8 = new javax.swing.JScrollPane();
		myDownloadTable = new javax.swing.JTable();
		refreshUnloadedFiles = new javax.swing.JButton();
		jScrollPane6 = new javax.swing.JScrollPane();
		undownloadedFileInfoTable = new javax.swing.JTable();
		jPanel9 = new javax.swing.JPanel();
		jScrollPane9 = new javax.swing.JScrollPane();
		loadedTable = new javax.swing.JTable();
		refreshLoadedFiles = new javax.swing.JButton();
		jScrollPane7 = new javax.swing.JScrollPane();
		loadedFileInfoTable = new javax.swing.JTable();
		jPanel5 = new javax.swing.JPanel();
		jScrollPane5 = new javax.swing.JScrollPane();
		viewer = new javax.swing.JList();
		jPanel6 = new javax.swing.JPanel();
		hostnameField = new javax.swing.JLabel();
		nicknameField = new javax.swing.JLabel();
		ipField = new javax.swing.JLabel();
		jPanel7 = new javax.swing.JPanel();
		jScrollPane2 = new javax.swing.JScrollPane();
		recommandTable = new javax.swing.JTable();
		jToolBar1 = new javax.swing.JToolBar();
		jButton1 = new javax.swing.JButton();
		jSeparator2 = new javax.swing.JToolBar.Separator();
		jButton3 = new javax.swing.JButton();
		jSeparator10 = new javax.swing.JToolBar.Separator();
		jButton4 = new javax.swing.JButton();
		jSeparator22 = new javax.swing.JToolBar.Separator();
		jButton5 = new javax.swing.JButton();
		jButton8 = new javax.swing.JButton();
		jButton9 = new javax.swing.JButton();
		jSeparator12 = new javax.swing.JToolBar.Separator();
		jButton7 = new javax.swing.JButton();
		jSeparator16 = new javax.swing.JToolBar.Separator();
		jButton2 = new javax.swing.JButton();
		jSeparator5 = new javax.swing.JToolBar.Separator();
		jButton6 = new javax.swing.JButton();
		jSeparator6 = new javax.swing.JToolBar.Separator();
		jButton10 = new javax.swing.JButton();
		jMenuBar1 = new javax.swing.JMenuBar();
		jMenu1 = new javax.swing.JMenu();
		MenuItemConnect = new javax.swing.JMenuItem();
		MenuItemDisconnect = new javax.swing.JMenuItem();
		jSeparator1 = new javax.swing.JSeparator();
		MenuItemExit = new javax.swing.JMenuItem();
		jMenu2 = new javax.swing.JMenu();
		jMenu4 = new javax.swing.JMenu();
		windowItem = new javax.swing.JMenuItem();
		swingItem = new javax.swing.JMenuItem();
		motifItem = new javax.swing.JMenuItem();
		autumnSkinItem = new javax.swing.JMenuItem();
		businessBlueSteelItem = new javax.swing.JMenuItem();
		businessBlackSteelItem = new javax.swing.JMenuItem();
		businessItem = new javax.swing.JMenuItem();
		challengerDeepItem = new javax.swing.JMenuItem();
		cremeItem = new javax.swing.JMenuItem();
		emeraldDuskItem = new javax.swing.JMenuItem();
		fieldOfWheatItem = new javax.swing.JMenuItem();
		greenMagicItem = new javax.swing.JMenuItem();
		magmaItem = new javax.swing.JMenuItem();
		mangoItem = new javax.swing.JMenuItem();
		mistAquaItem = new javax.swing.JMenuItem();
		mistSilverItem = new javax.swing.JMenuItem();
		moderateItem = new javax.swing.JMenuItem();
		nebulaBrickWallItem = new javax.swing.JMenuItem();
		nebulaItem = new javax.swing.JMenuItem();
		officeBlue2007Item = new javax.swing.JMenuItem();
		officeSilver2007Item = new javax.swing.JMenuItem();
		ravenGraphiteGlassItem = new javax.swing.JMenuItem();
		ravenGraphiteItem = new javax.swing.JMenuItem();
		ravenItem = new javax.swing.JMenuItem();
		saharaItem = new javax.swing.JMenuItem();
		jMenu3 = new javax.swing.JMenu();
		ItemAboutMe = new javax.swing.JMenuItem();

		connectResItem
				.setIcon(new javax.swing.ImageIcon(
						".\\icons\\右键续接资源.png")); // NOI18N
		connectResItem.setText("\u7eed\u63a5\u8d44\u6e90");
		connectResItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				connectResItemActionPerformed(evt);
			}
		});
		downloadPop.add(connectResItem);
		downloadPop.add(jSeparator3);

		disconnectResItem
				.setIcon(new javax.swing.ImageIcon(
						".\\icons\\右键停止续接.png")); // NOI18N
		disconnectResItem.setText("\u505c\u6b62\u7eed\u63a5");
		disconnectResItem
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						disconnectResItemActionPerformed(evt);
					}
				});
		downloadPop.add(disconnectResItem);
		downloadPop.add(jSeparator8);

		contDownloadItem
				.setIcon(new javax.swing.ImageIcon(
						".\\icons\\右键继续下载.png")); // NOI18N
		contDownloadItem.setText("\u7ee7\u7eed\u4e0b\u8f7d");
		contDownloadItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				contDownloadItemActionPerformed(evt);
			}
		});
		downloadPop.add(contDownloadItem);
		downloadPop.add(jSeparator9);

		pauseDownloadItem
				.setIcon(new javax.swing.ImageIcon(
						".\\icons\\右键暂停下载.png")); // NOI18N
		pauseDownloadItem.setText("\u6682\u505c\u4e0b\u8f7d");
		pauseDownloadItem
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						pauseDownloadItemActionPerformed(evt);
					}
				});
		downloadPop.add(pauseDownloadItem);
		downloadPop.add(jSeparator13);

		deleteUnloadedfileItem
				.setIcon(new javax.swing.ImageIcon(
						".\\icons\\右键删除.png")); // NOI18N
		deleteUnloadedfileItem.setText("\u5220\u9664");
		deleteUnloadedfileItem
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						deleteUnloadedfileItemActionPerformed(evt);
					}
				});
		downloadPop.add(deleteUnloadedfileItem);

		downloadPop.getAccessibleContext().setAccessibleName("downloadJMenu");

		openFileItem
				.setIcon(new javax.swing.ImageIcon(
						".\\icons\\右键打开文件.png")); // NOI18N
		openFileItem.setText("\u6253\u5f00");
		openFileItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				openFileItemActionPerformed(evt);
			}
		});
		downloadedTablePop.add(openFileItem);
		downloadedTablePop.add(jSeparator19);

		deleteItem
				.setIcon(new javax.swing.ImageIcon(
						".\\icons\\右键删除.png")); // NOI18N
		deleteItem.setText("\u5220\u9664\u6587\u4ef6");
		deleteItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				deleteItemActionPerformed(evt);
			}
		});
		downloadedTablePop.add(deleteItem);
		downloadedTablePop.add(jSeparator20);

		openFolderItem
				.setIcon(new javax.swing.ImageIcon(
						".\\icons\\右键打开文件夹.png")); // NOI18N
		openFolderItem.setText("\u6253\u5f00\u6587\u4ef6\u5939");
		openFolderItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				openFolderItemActionPerformed(evt);
			}
		});
		downloadedTablePop.add(openFolderItem);
		downloadedTablePop.add(jSeparator21);

		refreshDownloadedFilesItem
				.setIcon(new javax.swing.ImageIcon(
						".\\icons\\右键刷新.png")); // NOI18N
		refreshDownloadedFilesItem.setText("\u5237\u65b0");
		refreshDownloadedFilesItem
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						refreshDownloadedFilesItemActionPerformed(evt);
					}
				});
		downloadedTablePop.add(refreshDownloadedFilesItem);

		sharedOpenFileItem
				.setIcon(new javax.swing.ImageIcon(
						".\\icons\\右键打开文件.png")); // NOI18N
		sharedOpenFileItem.setText("\u6253\u5f00\u6587\u4ef6");
		sharedOpenFileItem
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						sharedOpenFileItemActionPerformed(evt);
					}
				});
		sharedTabelPop.add(sharedOpenFileItem);
		sharedTabelPop.add(jSeparator14);

		deletedSharedFileItem
				.setIcon(new javax.swing.ImageIcon(
						".\\icons\\右键删除.png")); // NOI18N
		deletedSharedFileItem.setText("\u5220\u9664\u6587\u4ef6");
		deletedSharedFileItem
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						deletedSharedFileItemActionPerformed(evt);
					}
				});
		sharedTabelPop.add(deletedSharedFileItem);
		sharedTabelPop.add(jSeparator15);

		sharedOpenFolderItem
				.setIcon(new javax.swing.ImageIcon(
						".\\icons\\右键打开文件夹.png")); // NOI18N
		sharedOpenFolderItem.setText("\u6253\u5f00\u6587\u4ef6\u5939");
		sharedOpenFolderItem
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						sharedOpenFolderItemActionPerformed(evt);
					}
				});
		sharedTabelPop.add(sharedOpenFolderItem);
		sharedTabelPop.add(jSeparator17);

		sharedFilesRereshItem
				.setIcon(new javax.swing.ImageIcon(
						".\\icons\\右键刷新.png")); // NOI18N
		sharedFilesRereshItem.setText("\u5237\u65b0");
		sharedFilesRereshItem
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						sharedFilesRereshItemActionPerformed(evt);
					}
				});
		sharedTabelPop.add(sharedFilesRereshItem);
		sharedTabelPop.add(jSeparator18);

		sharedFilesAddItem
				.setIcon(new javax.swing.ImageIcon(
						".\\icons\\右键添加文件.png")); // NOI18N
		sharedFilesAddItem.setText("\u6dfb\u52a0\u5206\u4eab");
		sharedFilesAddItem
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						sharedFilesAddItemActionPerformed(evt);
					}
				});
		sharedTabelPop.add(sharedFilesAddItem);
		sharedTabelPop.add(jSeparator7);

		unshareFileItem
				.setIcon(new javax.swing.ImageIcon(
						".\\icons\\停止分享.png")); // NOI18N
		unshareFileItem.setText("\u53d6\u6d88\u5206\u4eab");
		unshareFileItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				unshareFileItemActionPerformed(evt);
			}
		});
		sharedTabelPop.add(unshareFileItem);

		exitItem.setText("Item");
		trayPop.add(exitItem);

		recommandItem
				.setIcon(new javax.swing.ImageIcon(
						".\\icons\\右键搜索.png")); // NOI18N
		recommandItem.setText("\u641c\u7d22");
		recommandItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				recommandItemActionPerformed(evt);
			}
		});
		recommandPop.add(recommandItem);

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("\u6587\u4ef6\u5171\u4eab\u8f6f\u4ef6");
		setLocationByPlatform(true);
		setResizable(false);
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent evt) {
				formWindowClosing(evt);
			}
		});

		jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.LEFT);

		jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

		txtLog.setColumns(20);
		txtLog.setEditable(false);
		txtLog.setRows(5);
		txtLog
				.setText("\u6b22\u8fce\u4f7f\u7528\u6587\u4ef6\u5171\u4eab\u7cfb\u7edf ");
		txtLog.setToolTipText("\u7cfb\u7edf\u65e5\u5fd7\u8bb0\u5f55");
		txtLog.setBorder(javax.swing.BorderFactory
				.createTitledBorder("\u7cfb\u7edf\u8bb0\u5f55"));
		txtLog.setSelectionColor(new java.awt.Color(51, 204, 0));
		jScrollPane1.setViewportView(txtLog);

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(
				jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				jPanel1Layout.createSequentialGroup().addContainerGap()
						.addComponent(jScrollPane1,
								javax.swing.GroupLayout.DEFAULT_SIZE, 715,
								Short.MAX_VALUE).addContainerGap()));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				jPanel1Layout.createSequentialGroup().addComponent(
						jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE,
						494, Short.MAX_VALUE).addContainerGap()));

		jTabbedPane1
				.addTab(
						"\u65e5\u5fd7",
						new javax.swing.ImageIcon(
								".\\icons\\日志tab.png"),
						jPanel1, "\u7cfb\u7edf\u8bb0\u5f55"); // NOI18N

		javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(
				jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				mySearchTabPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 739,
				Short.MAX_VALUE));
		jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				mySearchTabPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 508,
				Short.MAX_VALUE));

		jTabbedPane1
				.addTab(
						"\u641c\u7d22",
						new javax.swing.ImageIcon(
								".\\icons\\搜索tab.png"),
						jPanel2, "\u641c\u7d22\u6587\u4ef6"); // NOI18N

		jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

		mySharedTable.setAutoCreateRowSorter(true);
		mySharedTable.setModel(new javax.swing.table.DefaultTableModel(
				new Object[][] { { null, null, null, null },
						{ null, null, null, null }, { null, null, null, null },
						{ null, null, null, null }, { null, null, null, null },
						{ null, null, null, null }, { null, null, null, null },
						{ null, null, null, null }, { null, null, null, null },
						{ null, null, null, null } }, new String[] { "文件名",
						"文件大小(Byte)", "MD5", "文件路径" }) {
			boolean[] canEdit = new boolean[] { false, false, false, false };

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return canEdit[columnIndex];
			}
		});
		mySharedTable.setToolTipText("\u5171\u4eab\u6587\u4ef6\u5217\u8868");
		mySharedTable.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				mySharedTableMouseClicked(evt);
			}

			public void mousePressed(java.awt.event.MouseEvent evt) {
				mySharedTableMousePressed(evt);
			}
		});
		jScrollPane3.setViewportView(mySharedTable);

		addSharedFiles
				.setIcon(new javax.swing.ImageIcon(
						".\\icons\\增加分享.png")); // NOI18N
		addSharedFiles.setText("\u589e\u52a0\u5206\u4eab");
		addSharedFiles.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				addSharedFilesActionPerformed(evt);
			}
		});

		btnReload
				.setIcon(new javax.swing.ImageIcon(
						".\\icons\\刷新.png")); // NOI18N
		btnReload.setMnemonic('R');
		btnReload.setText("\u5237\u65b0");
		btnReload.setToolTipText("Refreshing Shared Files.");
		btnReload.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnReloadActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(
				jPanel3);
		jPanel3.setLayout(jPanel3Layout);
		jPanel3Layout
				.setHorizontalGroup(jPanel3Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(jSeparator4,
								javax.swing.GroupLayout.DEFAULT_SIZE, 735,
								Short.MAX_VALUE)
						.addGroup(
								jPanel3Layout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(addSharedFiles)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(btnReload)
										.addContainerGap(545, Short.MAX_VALUE))
						.addGroup(
								jPanel3Layout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(
												jScrollPane3,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												715, Short.MAX_VALUE)
										.addContainerGap()));
		jPanel3Layout
				.setVerticalGroup(jPanel3Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel3Layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jPanel3Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.TRAILING)
														.addComponent(
																btnReload,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																21,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																addSharedFiles,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																21,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												jScrollPane3,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												451,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(26, 26, 26)
										.addComponent(
												jSeparator4,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												10,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addContainerGap()));

		jTabbedPane1
				.addTab(
						"\u5171\u4eab",
						new javax.swing.ImageIcon(
								".\\icons\\共享tab.png"),
						jPanel3, "\u5171\u4eab\u6587\u4ef6"); // NOI18N

		jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

		myDownloadTable.setModel(new javax.swing.table.DefaultTableModel(
				new Object[][] { { null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null } },
				new String[] { "文件名", "文件大小(KB)", "百分比", "MD5", "状态", "连接状态",
						"资源数" }));
		myDownloadTable.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				myDownloadTableMousePressed(evt);
			}
		});
		jScrollPane8.setViewportView(myDownloadTable);

		refreshUnloadedFiles
				.setIcon(new javax.swing.ImageIcon(
						".\\icons\\刷新.png")); // NOI18N
		refreshUnloadedFiles.setText("\u5237\u65b0");
		refreshUnloadedFiles
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						refreshUnloadedFilesActionPerformed(evt);
					}
				});

		undownloadedFileInfoTable
				.setModel(new javax.swing.table.DefaultTableModel(
						new Object[][] { { "文件名", null }, { "大小", null },
								{ "类型", null }, { "剩余", null },
								{ "文件ID", null }, { "文件夹", null },
								{ "已传输", null } }, new String[] { "名称", "值" }) {
					boolean[] canEdit = new boolean[] { false, false };

					public boolean isCellEditable(int rowIndex, int columnIndex) {
						return canEdit[columnIndex];
					}
				});
		undownloadedFileInfoTable.setAutoscrolls(false);
		jScrollPane6.setViewportView(undownloadedFileInfoTable);

		javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(
				jPanel8);
		jPanel8.setLayout(jPanel8Layout);
		jPanel8Layout
				.setHorizontalGroup(jPanel8Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								javax.swing.GroupLayout.Alignment.TRAILING,
								jPanel8Layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jPanel8Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.TRAILING)
														.addComponent(
																jScrollPane8,
																javax.swing.GroupLayout.Alignment.LEADING,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																700,
																Short.MAX_VALUE)
														.addComponent(
																refreshUnloadedFiles,
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																jScrollPane6,
																javax.swing.GroupLayout.Alignment.LEADING,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																700,
																Short.MAX_VALUE))
										.addContainerGap()));
		jPanel8Layout
				.setVerticalGroup(jPanel8Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel8Layout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(refreshUnloadedFiles)
										.addGap(10, 10, 10)
										.addComponent(
												jScrollPane8,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												263, Short.MAX_VALUE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(
												jScrollPane6,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												134,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addContainerGap()));

		downloadTabbedPanel
				.addTab(
						"\u4e0b\u8f7d\u4e2d",
						new javax.swing.ImageIcon(
								".\\icons\\下载中tab.png"),
						jPanel8); // NOI18N
		jPanel8.getAccessibleContext().setAccessibleName("");

		loadedTable.setModel(new javax.swing.table.DefaultTableModel(
				new Object[][] { { null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null },
						{ null, null, null, null, null, null, null } },
				new String[] { "文件名", "文件大小(KB)", "已下载", "MD5", "状态", "完成时间",
						"文件路径" }) {
			boolean[] canEdit = new boolean[] { false, false, false, false,
					false, false, false };

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return canEdit[columnIndex];
			}
		});
		loadedTable.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				loadedTableMouseClicked(evt);
			}

			public void mousePressed(java.awt.event.MouseEvent evt) {
				loadedTableMousePressed(evt);
			}
		});
		jScrollPane9.setViewportView(loadedTable);

		refreshLoadedFiles
				.setIcon(new javax.swing.ImageIcon(
						".\\icons\\刷新.png")); // NOI18N
		refreshLoadedFiles.setText("\u5237\u65b0");
		refreshLoadedFiles
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						refreshLoadedFilesActionPerformed(evt);
					}
				});

		loadedFileInfoTable.setModel(new javax.swing.table.DefaultTableModel(
				new Object[][] { { "文件名", "" }, { "文件ID", null },
						{ "文件大小", null }, { "文件路径", null }, { "文件类型", null } },
				new String[] { "名称", "值" }) {
			boolean[] canEdit = new boolean[] { false, false };

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return canEdit[columnIndex];
			}
		});
		jScrollPane7.setViewportView(loadedFileInfoTable);

		javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(
				jPanel9);
		jPanel9.setLayout(jPanel9Layout);
		jPanel9Layout
				.setHorizontalGroup(jPanel9Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel9Layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jPanel9Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																jScrollPane9,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																700,
																Short.MAX_VALUE)
														.addComponent(
																jScrollPane7,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																700,
																Short.MAX_VALUE)
														.addComponent(
																refreshLoadedFiles))
										.addContainerGap()));
		jPanel9Layout
				.setVerticalGroup(jPanel9Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel9Layout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(refreshLoadedFiles)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												jScrollPane9,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												306, Short.MAX_VALUE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(
												jScrollPane7,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												105,
												javax.swing.GroupLayout.PREFERRED_SIZE)));

		downloadTabbedPanel
				.addTab(
						"\u5df2\u4e0b\u8f7d",
						new javax.swing.ImageIcon(
								".\\icons\\下载完成tab.png"),
						jPanel9, "null"); // NOI18N

		javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(
				jPanel4);
		jPanel4.setLayout(jPanel4Layout);
		jPanel4Layout.setHorizontalGroup(jPanel4Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				javax.swing.GroupLayout.Alignment.TRAILING,
				jPanel4Layout.createSequentialGroup().addComponent(
						downloadTabbedPanel,
						javax.swing.GroupLayout.DEFAULT_SIZE, 725,
						Short.MAX_VALUE).addContainerGap()));
		jPanel4Layout.setVerticalGroup(jPanel4Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				jPanel4Layout.createSequentialGroup().addComponent(
						downloadTabbedPanel,
						javax.swing.GroupLayout.DEFAULT_SIZE, 494,
						Short.MAX_VALUE).addContainerGap()));

		downloadTabbedPanel.getAccessibleContext().setAccessibleName(
				"Downloading");

		jTabbedPane1
				.addTab(
						"\u4e0b\u8f7d",
						new javax.swing.ImageIcon(
								".\\icons\\下载tab.png"),
						jPanel4, "\u4e0b\u8f7d\u4fe1\u606f"); // NOI18N
		jPanel4.getAccessibleContext().setAccessibleName(
				"\u4e0b\u8f7d\u4fe1\u606f");

		jPanel5.setBorder(javax.swing.BorderFactory
				.createTitledBorder("DDM\u7f51\u7edc\u7528\u6237"));

		viewer.setBorder(javax.swing.BorderFactory
				.createTitledBorder("\u7528\u6237\u5217\u8868:"));
		viewer.setModel(new javax.swing.AbstractListModel() {
			String[] strings = { "", "" };

			public int getSize() {
				return strings.length;
			}

			public Object getElementAt(int i) {
				return strings[i];
			}
		});
		viewer.setToolTipText("\u597d\u53cb\u5217\u8868");
		viewer.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				viewerMouseClicked(evt);
			}
		});
		jScrollPane5.setViewportView(viewer);

		jPanel6.setBorder(javax.swing.BorderFactory
				.createTitledBorder("\u597d\u53cb\u4fe1\u606f"));

		hostnameField.setBackground(new java.awt.Color(255, 255, 255));
		hostnameField.setBorder(javax.swing.BorderFactory
				.createTitledBorder("\u4e3b\u673a\u540d"));

		nicknameField.setBorder(javax.swing.BorderFactory
				.createTitledBorder("\u6635\u79f0"));

		ipField.setBorder(javax.swing.BorderFactory
				.createTitledBorder("IP\u5730\u5740"));

		javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(
				jPanel6);
		jPanel6.setLayout(jPanel6Layout);
		jPanel6Layout
				.setHorizontalGroup(jPanel6Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel6Layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jPanel6Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																hostnameField,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																231,
																Short.MAX_VALUE)
														.addComponent(
																nicknameField,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																231,
																Short.MAX_VALUE)
														.addComponent(
																ipField,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																231,
																Short.MAX_VALUE))
										.addGap(195, 195, 195)));
		jPanel6Layout
				.setVerticalGroup(jPanel6Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel6Layout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(
												hostnameField,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												52,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(
												nicknameField,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												52,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(18, 18, 18)
										.addComponent(
												ipField,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												52,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addContainerGap(20, Short.MAX_VALUE)));

		javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(
				jPanel5);
		jPanel5.setLayout(jPanel5Layout);
		jPanel5Layout
				.setHorizontalGroup(jPanel5Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel5Layout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(
												jScrollPane5,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												245,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												jPanel6,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.addContainerGap()));
		jPanel5Layout
				.setVerticalGroup(jPanel5Layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanel5Layout
										.createSequentialGroup()
										.addGroup(
												jPanel5Layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(
																jScrollPane5,
																javax.swing.GroupLayout.Alignment.TRAILING,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																469,
																Short.MAX_VALUE)
														.addComponent(
																jPanel6,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addContainerGap()));

		jTabbedPane1
				.addTab(
						"\u597d\u53cb",
						new javax.swing.ImageIcon(
								".\\icons\\好友tab.png"),
						jPanel5, "\u597d\u53cb\u7ba1\u7406"); // NOI18N

		recommandTable.setModel(new javax.swing.table.DefaultTableModel(
				new Object[][] { { "let it be me" }, { "爱情转移" }, { "你的背包" },
						{ "yellow" } }, new String[] { "文件名" }));
		recommandTable.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent evt) {
				recommandTableMousePressed(evt);
			}
		});
		jScrollPane2.setViewportView(recommandTable);

		javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(
				jPanel7);
		jPanel7.setLayout(jPanel7Layout);
		jPanel7Layout.setHorizontalGroup(jPanel7Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				jPanel7Layout.createSequentialGroup().addContainerGap()
						.addComponent(jScrollPane2,
								javax.swing.GroupLayout.DEFAULT_SIZE, 729,
								Short.MAX_VALUE)));
		jPanel7Layout.setVerticalGroup(jPanel7Layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 508,
				Short.MAX_VALUE));

		jTabbedPane1
				.addTab(
						"\u63a8\u8350",
						new javax.swing.ImageIcon(
								".\\icons\\推荐tab.png"),
						jPanel7); // NOI18N

		jToolBar1.setRollover(true);

		jButton1
				.setIcon(new javax.swing.ImageIcon(
						".\\icons\\日志工具.png")); // NOI18N
		jButton1.setToolTipText("\u7cfb\u7edf\u8bb0\u5f55");
		jButton1.setFocusable(false);
		jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		jButton1.setMaximumSize(new java.awt.Dimension(30, 30));
		jButton1.setMinimumSize(new java.awt.Dimension(30, 30));
		jButton1.setPreferredSize(new java.awt.Dimension(30, 30));
		jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		jButton1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton1ActionPerformed(evt);
			}
		});
		jToolBar1.add(jButton1);
		jToolBar1.add(jSeparator2);

		jButton3
				.setIcon(new javax.swing.ImageIcon(
						".\\icons\\搜索工具.png")); // NOI18N
		jButton3.setToolTipText("\u641c\u7d22\u6587\u4ef6");
		jButton3.setFocusable(false);
		jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		jButton3.setMaximumSize(new java.awt.Dimension(30, 30));
		jButton3.setMinimumSize(new java.awt.Dimension(30, 30));
		jButton3.setPreferredSize(new java.awt.Dimension(30, 30));
		jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		jButton3.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton3ActionPerformed(evt);
			}
		});
		jToolBar1.add(jButton3);
		jToolBar1.add(jSeparator10);

		jButton4
				.setIcon(new javax.swing.ImageIcon(
						".\\icons\\共享工具.png")); // NOI18N
		jButton4.setToolTipText("\u5171\u4eab\u7ba1\u7406");
		jButton4.setFocusable(false);
		jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		jButton4.setMaximumSize(new java.awt.Dimension(30, 30));
		jButton4.setMinimumSize(new java.awt.Dimension(30, 30));
		jButton4.setPreferredSize(new java.awt.Dimension(30, 30));
		jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		jButton4.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton4ActionPerformed(evt);
			}
		});
		jToolBar1.add(jButton4);
		jToolBar1.add(jSeparator22);

		jButton5
				.setIcon(new javax.swing.ImageIcon(
						".\\icons\\下载工具.png")); // NOI18N
		jButton5.setToolTipText("\u4e0b\u8f7d\u7ba1\u7406");
		jButton5.setFocusable(false);
		jButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		jButton5.setMaximumSize(new java.awt.Dimension(30, 30));
		jButton5.setMinimumSize(new java.awt.Dimension(30, 30));
		jButton5.setPreferredSize(new java.awt.Dimension(30, 30));
		jButton5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		jButton5.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton5ActionPerformed(evt);
			}
		});
		jToolBar1.add(jButton5);

		jButton8
				.setIcon(new javax.swing.ImageIcon(
						".\\icons\\正在下载工具.png")); // NOI18N
		jButton8.setToolTipText("\u4e0b\u8f7d\u4e2d");
		jButton8.setFocusable(false);
		jButton8.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		jButton8.setMaximumSize(new java.awt.Dimension(30, 30));
		jButton8.setMinimumSize(new java.awt.Dimension(30, 30));
		jButton8.setPreferredSize(new java.awt.Dimension(30, 30));
		jButton8.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		jButton8.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton8ActionPerformed(evt);
			}
		});
		jToolBar1.add(jButton8);

		jButton9
				.setIcon(new javax.swing.ImageIcon(
						".\\icons\\下载完成工具.png")); // NOI18N
		jButton9.setToolTipText("\u5df2\u4e0b\u8f7d");
		jButton9.setFocusable(false);
		jButton9.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		jButton9.setMaximumSize(new java.awt.Dimension(30, 30));
		jButton9.setMinimumSize(new java.awt.Dimension(30, 30));
		jButton9.setPreferredSize(new java.awt.Dimension(30, 30));
		jButton9.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		jButton9.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton9ActionPerformed(evt);
			}
		});
		jToolBar1.add(jButton9);
		jToolBar1.add(jSeparator12);

		jButton7
				.setIcon(new javax.swing.ImageIcon(
						".\\icons\\好友工具.png")); // NOI18N
		jButton7.setToolTipText("\u597d\u53cb\u7ba1\u7406");
		jButton7.setFocusable(false);
		jButton7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		jButton7.setMaximumSize(new java.awt.Dimension(30, 30));
		jButton7.setMinimumSize(new java.awt.Dimension(30, 30));
		jButton7.setPreferredSize(new java.awt.Dimension(30, 30));
		jButton7.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		jButton7.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton7ActionPerformed(evt);
			}
		});
		jToolBar1.add(jButton7);
		jToolBar1.add(jSeparator16);

		jButton2
				.setIcon(new javax.swing.ImageIcon(
						".\\icons\\打开共享文件夹工具.png")); // NOI18N
		jButton2.setToolTipText("\u6253\u5f00\u5171\u4eab\u6587\u4ef6\u5939");
		jButton2.setFocusable(false);
		jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		jButton2.setMaximumSize(new java.awt.Dimension(30, 30));
		jButton2.setMinimumSize(new java.awt.Dimension(30, 30));
		jButton2.setPreferredSize(new java.awt.Dimension(30, 30));
		jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		jButton2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton2ActionPerformed(evt);
			}
		});
		jToolBar1.add(jButton2);
		jToolBar1.add(jSeparator5);

		jButton6
				.setIcon(new javax.swing.ImageIcon(
						".\\icons\\设置工具.png")); // NOI18N
		jButton6.setToolTipText("\u7cfb\u7edf\u8bbe\u7f6e");
		jButton6.setFocusable(false);
		jButton6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		jButton6.setMaximumSize(new java.awt.Dimension(30, 30));
		jButton6.setMinimumSize(new java.awt.Dimension(30, 30));
		jButton6.setPreferredSize(new java.awt.Dimension(30, 30));
		jButton6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		jButton6.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton6ActionPerformed(evt);
			}
		});
		jToolBar1.add(jButton6);
		jToolBar1.add(jSeparator6);

		jButton10
				.setIcon(new javax.swing.ImageIcon(
						".\\icons\\推荐工具.png")); // NOI18N
		jButton10.setToolTipText("\u63a8\u8350");
		jButton10.setFocusable(false);
		jButton10.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		jButton10.setMaximumSize(new java.awt.Dimension(30, 30));
		jButton10.setMinimumSize(new java.awt.Dimension(30, 30));
		jButton10.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		jButton10.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton10ActionPerformed(evt);
			}
		});
		jToolBar1.add(jButton10);

		jMenu1.setMnemonic('F');
		jMenu1.setText("\u6587\u4ef6");

		MenuItemConnect.setMnemonic('C');
		MenuItemConnect.setText("\u8fde\u63a5");
		MenuItemConnect.setEnabled(false);
		MenuItemConnect.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				MenuItemConnectActionPerformed(evt);
			}
		});
		jMenu1.add(MenuItemConnect);

		MenuItemDisconnect.setText("\u65ad\u5f00");
		MenuItemDisconnect
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						MenuItemDisconnectActionPerformed(evt);
					}
				});
		jMenu1.add(MenuItemDisconnect);
		jMenu1.add(jSeparator1);

		MenuItemExit.setMnemonic('X');
		MenuItemExit.setText("\u9000\u51fa");
		MenuItemExit.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				MenuItemExitActionPerformed(evt);
			}
		});
		jMenu1.add(MenuItemExit);

		jMenuBar1.add(jMenu1);

		jMenu2.setMnemonic('V');
		jMenu2.setText("\u76ae\u80a4\u7ba1\u7406");

		jMenu4.setText("\u76ae\u80a4");

		windowItem.setText("Windows");
		windowItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				windowItemActionPerformed(evt);
			}
		});
		jMenu4.add(windowItem);

		swingItem.setText("Swing");
		swingItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				swingItemActionPerformed(evt);
			}
		});
		jMenu4.add(swingItem);

		motifItem.setText("Motif");
		motifItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				motifItemActionPerformed(evt);
			}
		});
		jMenu4.add(motifItem);

		autumnSkinItem.setText("Autumn");
		autumnSkinItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				autumnSkinItemActionPerformed(evt);
			}
		});
		jMenu4.add(autumnSkinItem);

		businessBlueSteelItem.setText("BusinessBlueSteel");
		businessBlueSteelItem.setActionCommand("Item");
		businessBlueSteelItem
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						businessBlueSteelItemActionPerformed(evt);
					}
				});
		jMenu4.add(businessBlueSteelItem);

		businessBlackSteelItem.setText("BusinessBlackSteel");
		businessBlackSteelItem
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						businessBlackSteelItemActionPerformed(evt);
					}
				});
		jMenu4.add(businessBlackSteelItem);

		businessItem.setText("Business");
		businessItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				businessItemActionPerformed(evt);
			}
		});
		jMenu4.add(businessItem);

		challengerDeepItem.setText("ChallengerDeep");
		challengerDeepItem
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						challengerDeepItemActionPerformed(evt);
					}
				});
		jMenu4.add(challengerDeepItem);

		cremeItem.setText("Creme");
		cremeItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cremeItemActionPerformed(evt);
			}
		});
		jMenu4.add(cremeItem);

		emeraldDuskItem.setText("EmeraldDusk");
		emeraldDuskItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				emeraldDuskItemActionPerformed(evt);
			}
		});
		jMenu4.add(emeraldDuskItem);

		fieldOfWheatItem.setText("FieldOfWheat");
		fieldOfWheatItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				fieldOfWheatItemActionPerformed(evt);
			}
		});
		jMenu4.add(fieldOfWheatItem);

		greenMagicItem.setText("GreenMagic");
		greenMagicItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				greenMagicItemActionPerformed(evt);
			}
		});
		jMenu4.add(greenMagicItem);

		magmaItem.setText("Magma");
		magmaItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				magmaItemActionPerformed(evt);
			}
		});
		jMenu4.add(magmaItem);

		mangoItem.setText("Mango");
		mangoItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				mangoItemActionPerformed(evt);
			}
		});
		jMenu4.add(mangoItem);

		mistAquaItem.setText("MistAqua");
		mistAquaItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				mistAquaItemActionPerformed(evt);
			}
		});
		jMenu4.add(mistAquaItem);

		mistSilverItem.setText("MistSilver");
		mistSilverItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				mistSilverItemActionPerformed(evt);
			}
		});
		jMenu4.add(mistSilverItem);

		moderateItem.setText("Moderate");
		moderateItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				moderateItemActionPerformed(evt);
			}
		});
		jMenu4.add(moderateItem);

		nebulaBrickWallItem.setText("NebulaBrickWall");
		nebulaBrickWallItem
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						nebulaBrickWallItemActionPerformed(evt);
					}
				});
		jMenu4.add(nebulaBrickWallItem);

		nebulaItem.setText("Nebula");
		nebulaItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				nebulaItemActionPerformed(evt);
			}
		});
		jMenu4.add(nebulaItem);

		officeBlue2007Item.setText("OfficeBlue2007");
		officeBlue2007Item
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						officeBlue2007ItemActionPerformed(evt);
					}
				});
		jMenu4.add(officeBlue2007Item);

		officeSilver2007Item.setText("OfficeSilver2007");
		officeSilver2007Item
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						officeSilver2007ItemActionPerformed(evt);
					}
				});
		jMenu4.add(officeSilver2007Item);

		ravenGraphiteGlassItem.setText("RavenGraphiteGlass");
		ravenGraphiteGlassItem
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						ravenGraphiteGlassItemActionPerformed(evt);
					}
				});
		jMenu4.add(ravenGraphiteGlassItem);

		ravenGraphiteItem.setText("RavenGraphite");
		ravenGraphiteItem
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						ravenGraphiteItemActionPerformed(evt);
					}
				});
		jMenu4.add(ravenGraphiteItem);

		ravenItem.setText("Raven");
		ravenItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				ravenItemActionPerformed(evt);
			}
		});
		jMenu4.add(ravenItem);

		saharaItem.setText("Sahara");
		saharaItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				saharaItemActionPerformed(evt);
			}
		});
		jMenu4.add(saharaItem);

		jMenu2.add(jMenu4);

		jMenuBar1.add(jMenu2);

		jMenu3.setMnemonic('H');
		jMenu3.setText("\u5e2e\u52a9");

		ItemAboutMe.setMnemonic('A');
		ItemAboutMe.setText("\u5173\u4e8e\u6211");
		ItemAboutMe.setToolTipText("About Me!");
		ItemAboutMe.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				ItemAboutMeActionPerformed(evt);
			}
		});
		jMenu3.add(ItemAboutMe);

		jMenuBar1.add(jMenu3);

		setJMenuBar(jMenuBar1);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				jToolBar1, javax.swing.GroupLayout.Alignment.TRAILING,
				javax.swing.GroupLayout.DEFAULT_SIZE, 838, Short.MAX_VALUE)
				.addGroup(
						layout.createSequentialGroup().addComponent(
								jTabbedPane1,
								javax.swing.GroupLayout.PREFERRED_SIZE, 828,
								javax.swing.GroupLayout.PREFERRED_SIZE)
								.addContainerGap(
										javax.swing.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)));
		layout
				.setVerticalGroup(layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								layout
										.createSequentialGroup()
										.addComponent(
												jToolBar1,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												35,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												jTabbedPane1,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												513, Short.MAX_VALUE).addGap(
												20, 20, 20)));

		pack();
	}// </editor-fold>
	//GEN-END:initComponents

	private void unshareFileItemActionPerformed(java.awt.event.ActionEvent evt) {
		if (sharedTableRow != -1) {
			System.out.println("释放共享文件");
			mySharedTable.setRowSelectionInterval(sharedTableRow,
					sharedTableRow);

			String md5 = mySharedTable.getValueAt(sharedTableRow, 2).toString();
			fileSharer.removeShareFiles(sharedTableRow);
			ShareXMLHelper.getInstance().removeSharedfilesInfo(md5);
			sharedFilesReader.readSharedFilesFromConfig();
		}
	}

	private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {
		this.jTabbedPane1.setSelectedIndex(5);
	}

	private void recommandItemActionPerformed(java.awt.event.ActionEvent evt) {
		if (recommandRow != -1) {
			String recommandKey = this.recommandTable.getValueAt(recommandRow,
					0).toString();
			if ((recommandKey != "")
					&& (!P2PForm.filesearcherThreads.containsKey(recommandKey))) {
				MyFileSearchTab searchPanel = new MyFileSearchTab(this.pg,
						this.txtLog, this, recommandKey);
				this.getSearchTabPanel().addTab("搜索:" + recommandKey,
						searchPanel);
				P2PForm.filesearcherPanels.put(recommandKey, searchPanel);
				FileSearcher fileSearcher = new FileSearcher(pg, recommandKey,
						this.txtLog, searchPanel.getSearchTable());
				fileSearcher.start();
				P2PForm.filesearcherThreads.put(recommandKey, fileSearcher);
				this.jTabbedPane1.setSelectedIndex(1);
			}
		}
	}

	private void recommandTableMousePressed(java.awt.event.MouseEvent evt) {
		if (SwingUtilities.isRightMouseButton(evt)) {
			recommandRow = recommandTable.rowAtPoint(evt.getPoint());
			recommandTable.clearSelection();
			recommandTable.addRowSelectionInterval(recommandRow, recommandRow);

			System.out.println("recommandRow:" + recommandRow);
			this.recommandPop.show(evt.getComponent(), evt.getX(), evt.getY());
		}
	}

	private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {
		SettinDialog settinDia = new SettinDialog(this, false);
		settinDia.setVisible(true);
	}

	private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {
		this.jTabbedPane1.setSelectedIndex(3);
		this.downloadTabbedPanel.setSelectedIndex(1);
	}

	private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {
		this.jTabbedPane1.setSelectedIndex(3);
		this.downloadTabbedPanel.setSelectedIndex(0);
	}

	private void refreshDownloadedFilesItemActionPerformed(
			java.awt.event.ActionEvent evt) {
		loadedFilesReader.readFilesFromConfig();
	}

	private void sharedFilesRereshItemActionPerformed(
			java.awt.event.ActionEvent evt) {
		//GEN-FIRST:event_btnReloadActionPerformed
		sharedFilesReader.readSharedFilesFromConfig();
	}

	private void sharedFilesAddItemActionPerformed(
			java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(true);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setDialogTitle("打开文件夹");
		int ret = fileChooser.showOpenDialog(null);
		if (ret == JFileChooser.APPROVE_OPTION) {
			fileSharer.addSharedFiles(fileChooser.getSelectedFiles());
		}
		sharedFilesReader.readSharedFilesFromConfig();
	}

	private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
		System.out.println("打开的共享文件夹是" + P2PForm.sharedpath);
		Desktop desktop = Desktop.getDesktop();
		File file = new File(P2PForm.sharedpath);
		if (!file.exists()) {
			JOptionPane.showMessageDialog(null, "对不起，该文件夹已经不存在！！", "Error",
					JOptionPane.ERROR_MESSAGE);
		} else {
			try {
				desktop.open(file);
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "抱歉，打开文件夹出现异常！！", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void viewerMouseClicked(java.awt.event.MouseEvent evt) {
		OnlineMessage message = null;
		if (viewer.getSelectedIndex() != -1) {
			if (evt.getClickCount() == 1) {
				for (int i = 0; i < peers.size(); i++) {
					message = peers.get(i);
					if (message.getHostName().equals(
							viewer.getSelectedValue().toString())) {
						break;
					}
				}
				if (message != null) {
					this.hostnameField.setText(message.getComputerName());
					this.nicknameField.setText(message.getUserName());
					this.ipField.setText(message.getIp());
				}

			}
			if (evt.getClickCount() == 2) {
				CreateChatsessionThread createChatsessionThread = new CreateChatsessionThread(
						pg, viewer);
				createChatsessionThread.execute();
			}
		}
	}

	private void mySharedTableMouseClicked(java.awt.event.MouseEvent evt) {
		if (evt.getClickCount() == 2) {
			int clickedRow = mySharedTable.rowAtPoint(evt.getPoint());
			if (clickedRow != -1) {
				String filepath = mySharedTable.getValueAt(clickedRow, 3)
						.toString();
				Desktop desktop = Desktop.getDesktop();
				File file = new File(filepath);
				if (!file.exists()) {
					JOptionPane.showMessageDialog(null, "对不起，该文件已经不存在！！",
							"Error", JOptionPane.ERROR_MESSAGE);
				} else {
					try {
						desktop.open(file);
					} catch (IOException e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(null, "抱歉，打开文件出现异常！！",
								"Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			} else {
				JOptionPane.showMessageDialog(null, "您没有正确选中文件！！", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void sharedOpenFolderItemActionPerformed(
			java.awt.event.ActionEvent evt) {
		if (sharedTableRow != -1) {
			String filepath = mySharedTable.getValueAt(sharedTableRow, 3)
					.toString();
			String filename = mySharedTable.getValueAt(sharedTableRow, 0)
					.toString();
			String folder = filepath.replaceAll("" + filename, "");
			System.out.println("打开的文件夹是" + folder);
			Desktop desktop = Desktop.getDesktop();
			File file = new File(folder);
			if (!file.exists()) {
				JOptionPane.showMessageDialog(null, "对不起，该文件夹已经不存在！！", "Error",
						JOptionPane.ERROR_MESSAGE);
			} else {
				try {
					desktop.open(file);
				} catch (IOException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "抱歉，打开文件夹出现异常！！",
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		} else {
			JOptionPane.showMessageDialog(null, "您没有正确选中文件！！", "Error",
					JOptionPane.ERROR_MESSAGE);
		}

	}

	private void sharedOpenFileItemActionPerformed(
			java.awt.event.ActionEvent evt) {
		if (sharedTableRow != -1) {
			String filepath = mySharedTable.getValueAt(sharedTableRow, 3)
					.toString();
			Desktop desktop = Desktop.getDesktop();
			File file = new File(filepath);
			if (!file.exists()) {
				JOptionPane.showMessageDialog(null, "对不起，该文件夹已经不存在！！", "Error",
						JOptionPane.ERROR_MESSAGE);
			} else {
				try {
					desktop.open(file);
				} catch (IOException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "抱歉，打开文件夹出现异常！！",
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		} else {
			JOptionPane.showMessageDialog(null, "您没有正确选中文件！！", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void mySharedTableMousePressed(java.awt.event.MouseEvent evt) {
		// TODO add your handling code here:
		if (SwingUtilities.isRightMouseButton(evt)) {
			sharedTableRow = mySharedTable.rowAtPoint(evt.getPoint());
			mySharedTable.clearSelection();
			mySharedTable.addRowSelectionInterval(sharedTableRow,
					sharedTableRow);

			System.out.println("sharedTableRow:" + sharedTableRow);
			sharedTabelPop.show(evt.getComponent(), evt.getX(), evt.getY());
		}
	}

	private void deletedSharedFileItemActionPerformed(
			java.awt.event.ActionEvent evt) {
		if (sharedTableRow != -1) {
			System.out.println("删除共享文件");
			mySharedTable.setRowSelectionInterval(sharedTableRow,
					sharedTableRow);

			int select = JOptionPane.showConfirmDialog(null, "您确定要从磁盘删除所选定文件",
					"DDM文件共享", JOptionPane.YES_NO_OPTION);
			{
				if (select == 0) {
					System.out.println("YES");
					String md5 = mySharedTable.getValueAt(sharedTableRow, 2)
							.toString();
					String filepath = mySharedTable.getValueAt(sharedTableRow,
							3).toString();
					System.out.println(filepath);
					File file = new File(filepath);
					if (file.exists()) {
						boolean d = file.delete();
						if (!d) {
							JOptionPane.showMessageDialog(null, "对不起，删除文件出错！！",
									"Error", JOptionPane.ERROR_MESSAGE);
						} else {
							fileSharer.removeShareFiles(sharedTableRow);
							DownLoadXMLHelper.getInstance().removeFileInfo(md5);
							unloadedFilesReader.readFilesFromConfig();
							ShareXMLHelper.getInstance().removeSharedfilesInfo(
									md5);
							sharedFilesReader.readSharedFilesFromConfig();
						}
					} else {
						JOptionPane.showMessageDialog(null, "对不起，该文件已经不存在！！",
								"Error", JOptionPane.ERROR_MESSAGE);
					}
				}
				if (select == 1) {
					System.out.println("NO");
				}
			}
		}
	}

	private void deleteUnloadedfileItemActionPerformed(
			java.awt.event.ActionEvent evt) {
		if (unloadedTableRow != -1) {
			System.out.println("删除下载中文件");
			myDownloadTable.setRowSelectionInterval(unloadedTableRow,
					unloadedTableRow);

			int select = JOptionPane.showConfirmDialog(null, "您确定要从磁盘删除所选定文件",
					"DDM文件共享", JOptionPane.YES_NO_OPTION);
			{
				if (select == 0) {
					System.out.println("YES");
					String md5 = myDownloadTable
							.getValueAt(unloadedTableRow, 3).toString();
					String filepath = sharedpath
							+ "\\"
							+ myDownloadTable.getValueAt(unloadedTableRow, 0)
									.toString();
					System.out.println(filepath);
					File file = new File(filepath);
					if (file.exists()) {
						boolean d = file.delete();
						if (!d) {
							JOptionPane.showMessageDialog(null, "对不起，删除文件出错！！",
									"Error", JOptionPane.ERROR_MESSAGE);
						} else {
							DownLoadXMLHelper.getInstance().removeFileInfo(md5);
							unloadedFilesReader.readFilesFromConfig();
							ShareXMLHelper.getInstance().removeSharedfilesInfo(
									md5);
							sharedFilesReader.readSharedFilesFromConfig();
						}
					} else {
						JOptionPane.showMessageDialog(null, "对不起，该文件已经不存在！！",
								"Error", JOptionPane.ERROR_MESSAGE);
					}
				}
				if (select == 1) {
					System.out.println("NO");
				}
			}

		}
	}

	private void openFolderItemActionPerformed(java.awt.event.ActionEvent evt) {
		if (loadedTableRow != -1) {
			String filepath = loadedTable.getValueAt(loadedTableRow, 6)
					.toString();
			String filename = loadedTable.getValueAt(loadedTableRow, 0)
					.toString();
			String folder = filepath.replaceAll("" + filename, "");
			System.out.println("打开的文件夹是" + folder);
			Desktop desktop = Desktop.getDesktop();
			File file = new File(folder);
			if (!file.exists()) {
				JOptionPane.showMessageDialog(null, "对不起，该文件夹已经不存在！！", "Error",
						JOptionPane.ERROR_MESSAGE);
			} else {
				try {
					desktop.open(file);
				} catch (IOException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "抱歉，打开文件夹出现异常！！",
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		} else {
			JOptionPane.showMessageDialog(null, "您没有正确选中文件！！", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void deleteItemActionPerformed(java.awt.event.ActionEvent evt) {
		int select = JOptionPane.showConfirmDialog(null, "您确定要从磁盘删除所选定文件",
				"DDM文件共享", JOptionPane.YES_NO_OPTION);
		{
			if (select == 0) {
				System.out.println("YES");
				String md5 = loadedTable.getValueAt(loadedTableRow, 3)
						.toString();
				String filepath = loadedTable.getValueAt(loadedTableRow, 6)
						.toString();
				File file = new File(filepath);
				if (file.exists()) {
					boolean d = file.delete();
					if (!d) {
						JOptionPane.showMessageDialog(null, "对不起，打开文件出错！！",
								"Error", JOptionPane.ERROR_MESSAGE);
					} else {
						DownLoadXMLHelper.getInstance().removeFileInfo(md5);
						loadedFilesReader.readFilesFromConfig();
						ShareXMLHelper.getInstance().removeSharedfilesInfo(md5);
						sharedFilesReader.readSharedFilesFromConfig();
					}
				} else {
					JOptionPane.showMessageDialog(null, "对不起，该文件已经不存在！！",
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			if (select == 1) {
				System.out.println("NO");
			}
		}
	}

	private void loadedTableMouseClicked(java.awt.event.MouseEvent evt) {
		// TODO add your handling code here:
		if (evt.getClickCount() == 2) {
			int clickedRow = loadedTable.rowAtPoint(evt.getPoint());
			if (clickedRow != -1) {
				String filepath = loadedTable.getValueAt(clickedRow, 6)
						.toString();
				Desktop desktop = Desktop.getDesktop();
				File file = new File(filepath);
				if (!file.exists()) {
					JOptionPane.showMessageDialog(null, "对不起，该文件已经不存在！！",
							"Error", JOptionPane.ERROR_MESSAGE);
				} else {
					try {
						desktop.open(file);
					} catch (IOException e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(null, "抱歉，打开文件出现异常！！",
								"Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			} else {
				JOptionPane.showMessageDialog(null, "您没有正确选中文件！！", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void loadedTableMousePressed(java.awt.event.MouseEvent evt) {
		// TODO add your handling code here:
		if (SwingUtilities.isRightMouseButton(evt)) {
			loadedTableRow = loadedTable.rowAtPoint(evt.getPoint());
			loadedTable.clearSelection();
			loadedTable.addRowSelectionInterval(loadedTableRow, loadedTableRow);
			loadedTable.setRowSelectionInterval(loadedTableRow, loadedTableRow);
			System.out.println("loadedTableRow:" + loadedTableRow);
			downloadedTablePop.show(evt.getComponent(), evt.getX(), evt.getY());
		} else if (SwingUtilities.isLeftMouseButton(evt)) {
			loadedTableRow = loadedTable.rowAtPoint(evt.getPoint());
			System.out.println("loadedTableRow:" + loadedTableRow);
		}
		loadedFileInfoTable.setValueAt(loadedTable
				.getValueAt(loadedTableRow, 0), 0, 1);
		loadedFileInfoTable.setValueAt(loadedTable
				.getValueAt(loadedTableRow, 3), 1, 1);
		loadedFileInfoTable.setValueAt(loadedTable
				.getValueAt(loadedTableRow, 1), 2, 1);
		loadedFileInfoTable.setValueAt(loadedTable
				.getValueAt(loadedTableRow, 6), 3, 1);
		loadedFileInfoTable.setValueAt(FileHelper.getFileType(loadedTable
				.getValueAt(loadedTableRow, 0).toString()), 4, 1);
	}

	private void openFileItemActionPerformed(java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
		if (loadedTableRow != -1) {
			String filepath = loadedTable.getValueAt(loadedTableRow, 6)
					.toString();
			Desktop desktop = Desktop.getDesktop();
			File file = new File(filepath);
			if (!file.exists()) {
				JOptionPane.showMessageDialog(null, "对不起，该文件已经不存在！！", "Error",
						JOptionPane.ERROR_MESSAGE);
			} else {
				try {
					desktop.open(file);
				} catch (IOException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "抱歉，打开文件出现异常！！",
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		} else {
			JOptionPane.showMessageDialog(null, "您没有正确选中文件！！", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void addSharedFilesActionPerformed(java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(true);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setDialogTitle("打开文件夹");
		int ret = fileChooser.showOpenDialog(null);
		if (ret == JFileChooser.APPROVE_OPTION) {
			fileSharer.addSharedFiles(fileChooser.getSelectedFiles());
		}
		sharedFilesReader.readSharedFilesFromConfig();
	}

	private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
		this.jTabbedPane1.setSelectedIndex(4);
	}

	private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
		this.jTabbedPane1.setSelectedIndex(3);
	}

	private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
		this.jTabbedPane1.setSelectedIndex(2);
	}

	private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
		this.jTabbedPane1.setSelectedIndex(1);
	}

	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
		this.jTabbedPane1.setSelectedIndex(0);
	}

	private void connectResItemActionPerformed(java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
		if (unloadedTableRow != -1) {
			System.out.println("续接资源");
			myDownloadTable.setRowSelectionInterval(unloadedTableRow,
					unloadedTableRow);
			resourceConnectActionPerformed(evt, unloadedTableRow);
		}
	}

	private void disconnectResItemActionPerformed(java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
		if (unloadedTableRow != -1) {
			System.out.println("停止续接");
			myDownloadTable.setRowSelectionInterval(unloadedTableRow,
					unloadedTableRow);
			resourceDisconnectActionPerformed(evt, unloadedTableRow);
		}
	}

	//关闭程序-------------------------------------------------------------------------
	/**
	 * 关闭中
	 * @param evt
	 */
	private void formWindowClosing(java.awt.event.WindowEvent evt) {
		System.out.println("正在进行关闭");
		//清理下载线程----------------------------------------------------------------
		Set keys = downloadThreads.keySet();
		Iterator it = keys.iterator();//遍历取出
		while (it.hasNext()) {
			String key = (String) it.next();
			FileDownloader download = (FileDownloader) downloadThreads.get(key);
			download.stopLoad();
			System.out.println("关闭");
		}
		downloadThreads.clear();
		//清理搜索线程-----------------------------------------------------------------
		keys = contsearchThreads.keySet();
		it = keys.iterator();//遍历取出
		while (it.hasNext()) {
			String key = (String) it.next();
			FileContSearcher download = (FileContSearcher) contsearchThreads
					.get(key);
			download.killThread();
			download = null;
			System.out.println("关闭");
		}
		downloadThreads.clear();

		//文件共享对象
		if (fileSharer != null) {
			fileSharer.stopCMS();
			fileSharer.stop();
		}

		if (!chatwins.isEmpty()) {
			Enumeration enumeration = chatwins.elements();
			if (enumeration.hasMoreElements()) {
				ChatWindow chatWindow = (ChatWindow) enumeration.nextElement();
				chatWindow.dispose();
			}
		}
		mcastOfflineMessage(pg);
		stopService();
		//manager.stopNetwork();

	}

	//下载模块-------------------------------------------------------------------------------
	/**
	 * 刷新正在下载列表响应
	 * @param evt
	 */
	private void refreshUnloadedFilesActionPerformed(
			java.awt.event.ActionEvent evt) {
		unloadedFilesReader.readFilesFromConfig();
	}

	/**
	 * 刷新已下载列表响应
	 * @param evt
	 */
	private void refreshLoadedFilesActionPerformed(
			java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
		loadedFilesReader.readFilesFromConfig();
	}

	/**
	 * 下载Table右键响应
	 * @param evt
	 */
	private void myDownloadTableMousePressed(java.awt.event.MouseEvent evt) {
		// TODO add your handling code here:
		if (SwingUtilities.isRightMouseButton(evt)) {
			unloadedTableRow = myDownloadTable.rowAtPoint(evt.getPoint());
			myDownloadTable.clearSelection();
			myDownloadTable.addRowSelectionInterval(unloadedTableRow,
					unloadedTableRow);
			myDownloadTable.setRowSelectionInterval(unloadedTableRow,
					unloadedTableRow);
			System.out.println("row:" + unloadedTableRow);
			if (myDownloadTable.getValueAt(unloadedTableRow, 5) == null) {
				connectResItem.setEnabled(false);
				disconnectResItem.setEnabled(false);
				contDownloadItem.setEnabled(false);
				pauseDownloadItem.setEnabled(false);
				deleteUnloadedfileItem.setEnabled(false);

			} else {
				if (myDownloadTable.getValueAt(unloadedTableRow, 5).equals(
						"disconnected")) {
					connectResItem.setEnabled(true);
					disconnectResItem.setEnabled(false);
					contDownloadItem.setEnabled(false);
					pauseDownloadItem.setEnabled(false);
					deleteUnloadedfileItem.setEnabled(true);
				} else if (myDownloadTable.getValueAt(unloadedTableRow, 5)
						.equals("connecting")) {
					connectResItem.setEnabled(false);
					disconnectResItem.setEnabled(true);
					contDownloadItem.setEnabled(false);
					pauseDownloadItem.setEnabled(false);
					deleteUnloadedfileItem.setEnabled(false);
				} else if (myDownloadTable.getValueAt(unloadedTableRow, 5)
						.equals("connected")) {
					connectResItem.setEnabled(false);
					disconnectResItem.setEnabled(false);
					deleteUnloadedfileItem.setEnabled(false);
					if (myDownloadTable.getValueAt(unloadedTableRow, 4).equals(
							"loading")) {
						contDownloadItem.setEnabled(false);
						pauseDownloadItem.setEnabled(true);
					} else {
						contDownloadItem.setEnabled(true);
						pauseDownloadItem.setEnabled(false);
					}
				}
			}
			downloadPop.show(evt.getComponent(), evt.getX(), evt.getY());
		} else if (SwingUtilities.isLeftMouseButton(evt)) {
			unloadedTableRow = myDownloadTable.rowAtPoint(evt.getPoint());
			System.out.println("unloadedTableRow:" + unloadedTableRow);
		}
		undownloadedFileInfoTable.setValueAt(myDownloadTable.getValueAt(
				unloadedTableRow, 0), 0, 1);
		undownloadedFileInfoTable.setValueAt(myDownloadTable.getValueAt(
				unloadedTableRow, 1)
				+ "(Bytes)", 1, 1);
		undownloadedFileInfoTable.setValueAt(FileHelper
				.getFileType(myDownloadTable.getValueAt(unloadedTableRow, 0)
						.toString()), 2, 1);

		int percent = Integer.parseInt(myDownloadTable.getValueAt(
				unloadedTableRow, 2).toString().replaceAll("%", ""));
		double size = Integer.parseInt(myDownloadTable.getValueAt(
				unloadedTableRow, 1).toString());
		double loaded = percent * size * Double.parseDouble("0.01");
		double unloaded = (100 - percent) * size * Double.parseDouble("0.01");
		undownloadedFileInfoTable.setValueAt(String.format("%.0f", loaded)
				+ "(Bytes)", 3, 1);
		undownloadedFileInfoTable.setValueAt(myDownloadTable.getValueAt(
				unloadedTableRow, 3), 4, 1);
		undownloadedFileInfoTable.setValueAt(P2PForm.sharedpath, 5, 1);
		undownloadedFileInfoTable.setValueAt(String.format("%.0f", unloaded)
				+ "(Bytes)", 6, 1);
	}

	private void pauseDownloadItemActionPerformed(java.awt.event.ActionEvent evt) {
		if (unloadedTableRow != -1) {
			System.out.println("暂停下载");
			myDownloadTable.setRowSelectionInterval(unloadedTableRow,
					unloadedTableRow);
			cancelDownloadActionPerformed(evt, unloadedTableRow);
		}
	}

	private void contDownloadItemActionPerformed(java.awt.event.ActionEvent evt) {
		if (unloadedTableRow != -1) {
			System.out.println("继续下载");
			myDownloadTable.setRowSelectionInterval(unloadedTableRow,
					unloadedTableRow);
			contDownloadActionPerformed(evt, unloadedTableRow);
		}
	}

	private void resourceDisconnectActionPerformed(
			java.awt.event.ActionEvent e, int row) {

		originalMD5 = this.myDownloadTable.getValueAt(row, 3).toString();
		myFileName = this.myDownloadTable.getValueAt(row, 0).toString();
		FileContSearcher contSearcher = (FileContSearcher) contsearchThreads
				.get(originalMD5);
		contSearcher.killThread();
		ContentAdvertisement[] searchedAdvs = contSearcher.getContentAdvs();
		if (searchedAdvs != null) {
			this.myDownloadTable.setValueAt("connected", row, 5);
		} else {
			this.myDownloadTable.setValueAt("disconnected", row, 5);
		}
	}

	/**
	 * 右键连接资源
	 * @param e
	 * @param row
	 */
	private void resourceConnectActionPerformed(java.awt.event.ActionEvent e,
			int row) {
		originalMD5 = this.myDownloadTable.getValueAt(row, 3).toString();
		myFileName = this.myDownloadTable.getValueAt(row, 0).toString();
		FileContSearcher contSearching = new FileContSearcher(connection
				.DDMGroup(), myFileName, originalMD5, this.myDownloadTable, row);
		contsearchThreads.put(originalMD5, contSearching);
		System.out.println(originalMD5 + "put into contsearchs");
		contsearchThreadsExecutor.execute(contSearching);
		this.myDownloadTable.setValueAt("connecting", row, 5);
	}

	/**
	 * 右键继续下载
	 * @param evt
	 * @param row
	 */
	private void contDownloadActionPerformed(java.awt.event.ActionEvent evt,
			int row) {//GEN-FIRST:event_btnDownloadActionPerformed
		originalMD5 = this.myDownloadTable.getValueAt(row, 3).toString();
		myFileName = this.myDownloadTable.getValueAt(row, 0).toString();

		FileContSearcher contSearching = (FileContSearcher) contsearchThreads
				.get(originalMD5);

		if (row != -1) {
			File saveTo = new File(sharedpath + File.separator + myFileName);
			FileDownloader download = null;
			if (!downloadThreads.containsKey(originalMD5)) {
				download = new FileDownloader(connection.DDMGroup(),
						contSearching.getContentAdvs(), saveTo, this.txtLog,
						DownLoadXMLHelper.getInstance()
								.getUnloadedFileRowByMD5(originalMD5),
						myDownloadTable, unloadedFilesReader, loadedFilesReader);
				downloadThreads.put(originalMD5, download);
				System.out.println("put into thread");
				download = (FileDownloader) downloadThreads.get(originalMD5);
				downloadThreadsExecutor.execute(download);
				contsearchThreads.remove(originalMD5);
			}
		} else {
			JOptionPane.showMessageDialog(null,
					"Please select Row first then Press Download!", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * 右键暂停下载
	 * @param evt
	 * @param row
	 */
	private void cancelDownloadActionPerformed(java.awt.event.ActionEvent evt,
			int row) {
		FileDownloader download = (FileDownloader) downloadThreads
				.get(this.myDownloadTable.getValueAt(row, 3));
		download.cancel();
		downloadThreads.remove(this.myDownloadTable.getValueAt(row, 3));
	}

	//发布共享文件模块-------------------------------------------------------------------
	/**
	 * 刷新共享文件
	 */
	private void btnReloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReloadActionPerformed
		sharedFilesReader.readSharedFilesFromConfig();
	}

	/**
	 * 开启聊天服务
	 */
	private void startService() {
		service = new Service[] { new JxtaMulticastSocketService(pg, viewer),
				new JxtaServerSocketService(pg, viewer) };

		serviceExecutor = Executors.newFixedThreadPool(service.length);

		for (int i = 0; i < service.length; i++)
			serviceExecutor.execute(service[i]);
	}

	private void stopService() {
		for (int i = 0; i < service.length; i++)
			service[i].shutdownAndAwaitTermination();
		ThreadPoolUtil.shutdownAndAwaitTermination(serviceExecutor);
	}

	/**
	 * 广播在线信息
	 */
	private void mcastOnlineMessage(PeerGroup pg) {
		try {
			JxtaMulticastSocket mcastSocket = new JxtaMulticastSocket(pg,
					JxtaMulticastSocketService.getSocketAdvertisement(pg));
			String msg = XmlSender.createOnlineMessage();

			DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg
					.length());
			mcastSocket.send(packet);
			mcastSocket.close();
		} catch (IOException e) {
			System.out.println("multicast peer node message failed");
			e.printStackTrace();
		}
	}

	/**
	 * Multicast offline message
	 */
	private void mcastOfflineMessage(PeerGroup pg) {
		try {
			JxtaMulticastSocket mcastSocket = new JxtaMulticastSocket(pg,
					JxtaMulticastSocketService.getSocketAdvertisement(pg));
			String msg = XmlSender.createOfflineMessage();

			DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg
					.length());
			mcastSocket.send(packet);
			mcastSocket.close();
		} catch (IOException e) {
			System.out.println("multicast peer node message failed");
			e.printStackTrace();
		}
	}

	//系统服务模块--------------------------------------------------------------------

	/**
	 * 连接
	 */
	private void MenuItemConnectActionPerformed(java.awt.event.ActionEvent evt) {

		MenuItemConnect.setEnabled(false);

		File myPath = new File(sharedpath);
		fileSharer = new FileSharer(connection.DDMGroup(), this.txtLog, myPath,
				sharedFilesReader);
		fileSharer.start();
	}

	/**
	 * 断开连接
	 * @param evt
	 */
	private void MenuItemDisconnectActionPerformed(
			java.awt.event.ActionEvent evt) {
		MenuItemConnect.setEnabled(true);
		fileSharer.stopCMS();
		fileSharer = null;
	}

	/**
	 * 退出程序
	 * @param evt
	 */
	private void MenuItemExitActionPerformed(java.awt.event.ActionEvent evt) {
		System.exit(0);
	}

	/**
	 * 改变为其他风格
	 * @param evt
	 */
	private void motifItemActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
			SwingUtilities.updateComponentTreeUI(this);
			SwingUtilities.updateComponentTreeUI(recommandPop);
			SwingUtilities.updateComponentTreeUI(sharedTabelPop);
			SwingUtilities.updateComponentTreeUI(downloadPop);
			SwingUtilities.updateComponentTreeUI(downloadedTablePop);
		} catch (Exception e) {
			System.err.println("Unable to change");
		}
	}

	/**
	 * 改变为Swing风格
	 * @param evt
	 */
	private void swingItemActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			UIManager.setLookAndFeel(new MetalLookAndFeel());
			SwingUtilities.updateComponentTreeUI(this);
			SwingUtilities.updateComponentTreeUI(recommandPop);
			SwingUtilities.updateComponentTreeUI(sharedTabelPop);
			SwingUtilities.updateComponentTreeUI(downloadPop);
			SwingUtilities.updateComponentTreeUI(downloadedTablePop);
		} catch (Exception e) {
			System.err.println("Unable to change");
		}
	}

	private void windowItemActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			SwingUtilities.updateComponentTreeUI(this);
			SwingUtilities.updateComponentTreeUI(recommandPop);
			SwingUtilities.updateComponentTreeUI(sharedTabelPop);
			SwingUtilities.updateComponentTreeUI(downloadPop);
			SwingUtilities.updateComponentTreeUI(downloadedTablePop);
		} catch (Exception e) {
			System.err.println("Unable to change");
		}
	}

	private void officeSilver2007ItemActionPerformed(
			java.awt.event.ActionEvent evt) {
		try {
			UIManager
					.setLookAndFeel(new SubstanceOfficeSilver2007LookAndFeel());
			SwingUtilities.updateComponentTreeUI(this);
			SwingUtilities.updateComponentTreeUI(recommandPop);
			SwingUtilities.updateComponentTreeUI(sharedTabelPop);
			SwingUtilities.updateComponentTreeUI(downloadPop);
			SwingUtilities.updateComponentTreeUI(downloadedTablePop);
		} catch (Exception e) {
			System.err.println("Unable to change");
		}
	}

	private void saharaItemActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			UIManager.setLookAndFeel(new SubstanceSaharaLookAndFeel());
			SwingUtilities.updateComponentTreeUI(this);
			SwingUtilities.updateComponentTreeUI(recommandPop);
			SwingUtilities.updateComponentTreeUI(sharedTabelPop);
			SwingUtilities.updateComponentTreeUI(downloadPop);
			SwingUtilities.updateComponentTreeUI(downloadedTablePop);
		} catch (Exception e) {
			System.err.println("Unable to change");
		}
	}

	void ravenItemActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			UIManager.setLookAndFeel(new SubstanceRavenLookAndFeel());
			SwingUtilities.updateComponentTreeUI(this);
			SwingUtilities.updateComponentTreeUI(recommandPop);
			SwingUtilities.updateComponentTreeUI(sharedTabelPop);
			SwingUtilities.updateComponentTreeUI(downloadPop);
			SwingUtilities.updateComponentTreeUI(downloadedTablePop);
		} catch (Exception e) {
			System.err.println("Unable to change");
		}
	}

	private void ravenGraphiteItemActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			UIManager.setLookAndFeel(new SubstanceRavenGraphiteLookAndFeel());
			SwingUtilities.updateComponentTreeUI(this);
			SwingUtilities.updateComponentTreeUI(recommandPop);
			SwingUtilities.updateComponentTreeUI(sharedTabelPop);
			SwingUtilities.updateComponentTreeUI(downloadPop);
			SwingUtilities.updateComponentTreeUI(downloadedTablePop);
		} catch (Exception e) {
			System.err.println("Unable to change");
		}
	}

	private void ravenGraphiteGlassItemActionPerformed(
			java.awt.event.ActionEvent evt) {
		try {
			UIManager
					.setLookAndFeel(new SubstanceRavenGraphiteGlassLookAndFeel());
			SwingUtilities.updateComponentTreeUI(this);
			SwingUtilities.updateComponentTreeUI(recommandPop);
			SwingUtilities.updateComponentTreeUI(sharedTabelPop);
			SwingUtilities.updateComponentTreeUI(downloadPop);
			SwingUtilities.updateComponentTreeUI(downloadedTablePop);
		} catch (Exception e) {
			System.err.println("Unable to change");
		}
	}

	private void officeBlue2007ItemActionPerformed(
			java.awt.event.ActionEvent evt) {
		try {
			UIManager.setLookAndFeel(new SubstanceOfficeBlue2007LookAndFeel());
			SwingUtilities.updateComponentTreeUI(this);
			SwingUtilities.updateComponentTreeUI(recommandPop);
			SwingUtilities.updateComponentTreeUI(sharedTabelPop);
			SwingUtilities.updateComponentTreeUI(downloadPop);
			SwingUtilities.updateComponentTreeUI(downloadedTablePop);
		} catch (Exception e) {
			System.err.println("Unable to change");
		}
	}

	void nebulaItemActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			UIManager.setLookAndFeel(new SubstanceNebulaLookAndFeel());
			SwingUtilities.updateComponentTreeUI(this);
			SwingUtilities.updateComponentTreeUI(recommandPop);
			SwingUtilities.updateComponentTreeUI(sharedTabelPop);
			SwingUtilities.updateComponentTreeUI(downloadPop);
			SwingUtilities.updateComponentTreeUI(downloadedTablePop);
		} catch (Exception e) {
			System.err.println("Unable to change");
		}
	}

	private void nebulaBrickWallItemActionPerformed(
			java.awt.event.ActionEvent evt) {
		try {
			UIManager.setLookAndFeel(new SubstanceNebulaBrickWallLookAndFeel());
			SwingUtilities.updateComponentTreeUI(this);
			SwingUtilities.updateComponentTreeUI(recommandPop);
			SwingUtilities.updateComponentTreeUI(sharedTabelPop);
			SwingUtilities.updateComponentTreeUI(downloadPop);
			SwingUtilities.updateComponentTreeUI(downloadedTablePop);
		} catch (Exception e) {
			System.err.println("Unable to change");
		}
	}

	private void moderateItemActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			UIManager.setLookAndFeel(new SubstanceModerateLookAndFeel());
			SwingUtilities.updateComponentTreeUI(this);
			SwingUtilities.updateComponentTreeUI(recommandPop);
			SwingUtilities.updateComponentTreeUI(sharedTabelPop);
			SwingUtilities.updateComponentTreeUI(downloadPop);
			SwingUtilities.updateComponentTreeUI(downloadedTablePop);
		} catch (Exception e) {
			System.err.println("Unable to change");
		}
	}

	private void mistSilverItemActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			UIManager.setLookAndFeel(new SubstanceMistSilverLookAndFeel());
			SwingUtilities.updateComponentTreeUI(this);
			SwingUtilities.updateComponentTreeUI(recommandPop);
			SwingUtilities.updateComponentTreeUI(sharedTabelPop);
			SwingUtilities.updateComponentTreeUI(downloadPop);
			SwingUtilities.updateComponentTreeUI(downloadedTablePop);
		} catch (Exception e) {
			System.err.println("Unable to change");
		}
	}

	private void mistAquaItemActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			UIManager.setLookAndFeel(new SubstanceMistAquaLookAndFeel());
			SwingUtilities.updateComponentTreeUI(this);
			SwingUtilities.updateComponentTreeUI(recommandPop);
			SwingUtilities.updateComponentTreeUI(sharedTabelPop);
			SwingUtilities.updateComponentTreeUI(downloadPop);
			SwingUtilities.updateComponentTreeUI(downloadedTablePop);
		} catch (Exception e) {
			System.err.println("Unable to change");
		}
	}

	void mangoItemActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			UIManager.setLookAndFeel(new SubstanceMangoLookAndFeel());
			SwingUtilities.updateComponentTreeUI(this);
			SwingUtilities.updateComponentTreeUI(recommandPop);
			SwingUtilities.updateComponentTreeUI(sharedTabelPop);
			SwingUtilities.updateComponentTreeUI(downloadPop);
			SwingUtilities.updateComponentTreeUI(downloadedTablePop);
		} catch (Exception e) {
			System.err.println("Unable to change");
		}
	}

	void magmaItemActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			UIManager.setLookAndFeel(new SubstanceMagmaLookAndFeel());
			SwingUtilities.updateComponentTreeUI(this);
			SwingUtilities.updateComponentTreeUI(recommandPop);
			SwingUtilities.updateComponentTreeUI(sharedTabelPop);
			SwingUtilities.updateComponentTreeUI(downloadPop);
			SwingUtilities.updateComponentTreeUI(downloadedTablePop);
		} catch (Exception e) {
			System.err.println("Unable to change");
		}
	}

	private void greenMagicItemActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			UIManager.setLookAndFeel(new SubstanceGreenMagicLookAndFeel());
			SwingUtilities.updateComponentTreeUI(this);
			SwingUtilities.updateComponentTreeUI(recommandPop);
			SwingUtilities.updateComponentTreeUI(sharedTabelPop);
			SwingUtilities.updateComponentTreeUI(downloadPop);
			SwingUtilities.updateComponentTreeUI(downloadedTablePop);
		} catch (Exception e) {
			System.err.println("Unable to change");
		}
	}

	private void fieldOfWheatItemActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			UIManager.setLookAndFeel(new SubstanceFieldOfWheatLookAndFeel());
			SwingUtilities.updateComponentTreeUI(this);
			SwingUtilities.updateComponentTreeUI(recommandPop);
			SwingUtilities.updateComponentTreeUI(sharedTabelPop);
			SwingUtilities.updateComponentTreeUI(downloadPop);
			SwingUtilities.updateComponentTreeUI(downloadedTablePop);
		} catch (Exception e) {
			System.err.println("Unable to change");
		}
	}

	private void emeraldDuskItemActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			UIManager.setLookAndFeel(new SubstanceEmeraldDuskLookAndFeel());
			SwingUtilities.updateComponentTreeUI(this);
			SwingUtilities.updateComponentTreeUI(recommandPop);
			SwingUtilities.updateComponentTreeUI(sharedTabelPop);
			SwingUtilities.updateComponentTreeUI(downloadPop);
			SwingUtilities.updateComponentTreeUI(downloadedTablePop);
		} catch (Exception e) {
			System.err.println("Unable to change");
		}
	}

	void cremeItemActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			UIManager.setLookAndFeel(new SubstanceCremeLookAndFeel());
			SwingUtilities.updateComponentTreeUI(this);
			SwingUtilities.updateComponentTreeUI(recommandPop);
			SwingUtilities.updateComponentTreeUI(sharedTabelPop);
			SwingUtilities.updateComponentTreeUI(downloadPop);
			SwingUtilities.updateComponentTreeUI(downloadedTablePop);
		} catch (Exception e) {
			System.err.println("Unable to change");
		}
	}

	private void challengerDeepItemActionPerformed(
			java.awt.event.ActionEvent evt) {
		try {
			UIManager.setLookAndFeel(new SubstanceChallengerDeepLookAndFeel());
			SwingUtilities.updateComponentTreeUI(this);
			SwingUtilities.updateComponentTreeUI(recommandPop);
			SwingUtilities.updateComponentTreeUI(sharedTabelPop);
			SwingUtilities.updateComponentTreeUI(downloadPop);
			SwingUtilities.updateComponentTreeUI(downloadedTablePop);
		} catch (Exception e) {
			System.err.println("Unable to change");
		}
	}

	private void businessItemActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			UIManager.setLookAndFeel(new SubstanceBusinessLookAndFeel());
			SwingUtilities.updateComponentTreeUI(this);
			SwingUtilities.updateComponentTreeUI(recommandPop);
			SwingUtilities.updateComponentTreeUI(sharedTabelPop);
			SwingUtilities.updateComponentTreeUI(downloadPop);
			SwingUtilities.updateComponentTreeUI(downloadedTablePop);
		} catch (Exception e) {
			System.err.println("Unable to change");
		}
	}

	private void businessBlackSteelItemActionPerformed(
			java.awt.event.ActionEvent evt) {
		try {
			UIManager
					.setLookAndFeel(new SubstanceBusinessBlackSteelLookAndFeel());
			SwingUtilities.updateComponentTreeUI(this);
			SwingUtilities.updateComponentTreeUI(recommandPop);
			SwingUtilities.updateComponentTreeUI(sharedTabelPop);
			SwingUtilities.updateComponentTreeUI(downloadPop);
			SwingUtilities.updateComponentTreeUI(downloadedTablePop);
		} catch (Exception e) {
			System.err.println("Unable to change");
		}
	}

	private void businessBlueSteelItemActionPerformed(
			java.awt.event.ActionEvent evt) {
		try {
			UIManager
					.setLookAndFeel(new SubstanceBusinessBlueSteelLookAndFeel());
			SwingUtilities.updateComponentTreeUI(this);
			SwingUtilities.updateComponentTreeUI(recommandPop);
			SwingUtilities.updateComponentTreeUI(sharedTabelPop);
			SwingUtilities.updateComponentTreeUI(downloadPop);
			SwingUtilities.updateComponentTreeUI(downloadedTablePop);
		} catch (Exception e) {
			System.err.println("Unable to change");
		}
	}

	private void autumnSkinItemActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			UIManager.setLookAndFeel(new SubstanceAutumnLookAndFeel());
			SwingUtilities.updateComponentTreeUI(this);
			SwingUtilities.updateComponentTreeUI(recommandPop);
			SwingUtilities.updateComponentTreeUI(sharedTabelPop);
			SwingUtilities.updateComponentTreeUI(downloadPop);
			SwingUtilities.updateComponentTreeUI(downloadedTablePop);

		} catch (Exception e) {
			System.err.println("Unable to change");
		}
	}

	/**
	 * 关于我
	 * @param evt
	 */
	private void ItemAboutMeActionPerformed(java.awt.event.ActionEvent evt) {
	}

	public static void main(String args[]) {

		P2PForm mainForm = new P2PForm();

		try {
			UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			SwingUtilities.updateComponentTreeUI(mainForm);
			SwingUtilities.updateComponentTreeUI(mainForm.recommandPop);
			SwingUtilities.updateComponentTreeUI(mainForm.sharedTabelPop);
			SwingUtilities.updateComponentTreeUI(mainForm.downloadPop);
			SwingUtilities.updateComponentTreeUI(mainForm.downloadedTablePop);
		} catch (Exception e) {
			System.err.println("Unable to change");
		}
		mainForm.setVisible(true);

		//核对共享配置
		FirsttimeChecker checkFirstTime = new FirsttimeChecker(mainForm.txtLog);
		if (checkFirstTime.isFirstTime()) {
			JOptionPane.showMessageDialog(null, "请选择您的共享文件夹", "共享文件夹配置",
					JOptionPane.INFORMATION_MESSAGE);
			checkFirstTime.searchForConfigFile();
			sharedpath = checkFirstTime.getSharedPath();
			System.out.println("Shared Folder is " + sharedpath);
		} else {
			checkFirstTime.searchForConfigFile();
			sharedpath = checkFirstTime.getSharedPath();
		}
		sharedFilesReader = new FileSharedInfoReader(mainForm.mySharedTable);

		//核对配置文件和系统磁盘文件的一致性---------------------------------------------

		//核对下载配置
		ConfigCheck.checkDowloadConfig();
		loadedFilesReader = new FileLoadedInfoReader(mainForm.loadedTable);
		unloadedFilesReader = new FileUnloadedInfoReader(
				mainForm.myDownloadTable);
		loadedFilesReader.readFilesFromConfig();
		unloadedFilesReader.readFilesFromConfig();

		//开启JXTA服务
		connection = new JXTAStarter(mainForm.txtLog);
		pg = connection.DDMGroup();

		//聊天模块------------------------------
		mainForm.startService();
		mainForm.mcastOnlineMessage(pg);

		MyFileSearchTab fileSearchPanel0 = new ddm.p2p.myfilesharing.view.MyFileSearchTab(
				pg, mainForm.txtLog, mainForm, "");
		mainForm.getSearchTabPanel().addTab("开始搜索", fileSearchPanel0);
		fileSearchPanel0.setVisible(true);

		//设置分享
		File myPath = new File(sharedpath);
		fileSharer = new FileSharer(connection.DDMGroup(), mainForm.txtLog,
				myPath, sharedFilesReader);
		fileSharer.start();

		System.out.println("Finally The SharedFolder is" + P2PForm.sharedpath);
		
		DDMService ddmservice = DDMService.getInstance();
		ddmservice.run();
	}

	//GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JMenuItem ItemAboutMe;
	private javax.swing.JMenuItem MenuItemConnect;
	private javax.swing.JMenuItem MenuItemDisconnect;
	private javax.swing.JMenuItem MenuItemExit;
	private javax.swing.JButton addSharedFiles;
	private javax.swing.JMenuItem autumnSkinItem;
	private javax.swing.JButton btnReload;
	private javax.swing.JMenuItem businessBlackSteelItem;
	private javax.swing.JMenuItem businessBlueSteelItem;
	private javax.swing.JMenuItem businessItem;
	private javax.swing.JMenuItem challengerDeepItem;
	private javax.swing.JMenuItem connectResItem;
	private javax.swing.JMenuItem contDownloadItem;
	private javax.swing.JMenuItem cremeItem;
	private javax.swing.JMenuItem deleteItem;
	private javax.swing.JMenuItem deleteUnloadedfileItem;
	private javax.swing.JMenuItem deletedSharedFileItem;
	private javax.swing.JMenuItem disconnectResItem;
	private javax.swing.JPopupMenu downloadPop;
	private javax.swing.JTabbedPane downloadTabbedPanel;
	private javax.swing.JPopupMenu downloadedTablePop;
	private javax.swing.JMenuItem emeraldDuskItem;
	private javax.swing.JMenuItem exitItem;
	private javax.swing.JMenuItem fieldOfWheatItem;
	private javax.swing.JMenuItem greenMagicItem;
	private javax.swing.JLabel hostnameField;
	private javax.swing.JLabel ipField;
	private javax.swing.JButton jButton1;
	private javax.swing.JButton jButton10;
	private javax.swing.JButton jButton2;
	private javax.swing.JButton jButton3;
	private javax.swing.JButton jButton4;
	private javax.swing.JButton jButton5;
	private javax.swing.JButton jButton6;
	private javax.swing.JButton jButton7;
	private javax.swing.JButton jButton8;
	private javax.swing.JButton jButton9;
	private javax.swing.JMenu jMenu1;
	private javax.swing.JMenu jMenu2;
	private javax.swing.JMenu jMenu3;
	private javax.swing.JMenu jMenu4;
	private javax.swing.JMenuBar jMenuBar1;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JPanel jPanel4;
	private javax.swing.JPanel jPanel5;
	private javax.swing.JPanel jPanel6;
	private javax.swing.JPanel jPanel7;
	private javax.swing.JPanel jPanel8;
	private javax.swing.JPanel jPanel9;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JScrollPane jScrollPane3;
	private javax.swing.JScrollPane jScrollPane5;
	private javax.swing.JScrollPane jScrollPane6;
	private javax.swing.JScrollPane jScrollPane7;
	private javax.swing.JScrollPane jScrollPane8;
	private javax.swing.JScrollPane jScrollPane9;
	private javax.swing.JSeparator jSeparator1;
	private javax.swing.JToolBar.Separator jSeparator10;
	private javax.swing.JToolBar.Separator jSeparator12;
	private javax.swing.JSeparator jSeparator13;
	private javax.swing.JSeparator jSeparator14;
	private javax.swing.JSeparator jSeparator15;
	private javax.swing.JToolBar.Separator jSeparator16;
	private javax.swing.JSeparator jSeparator17;
	private javax.swing.JSeparator jSeparator18;
	private javax.swing.JSeparator jSeparator19;
	private javax.swing.JToolBar.Separator jSeparator2;
	private javax.swing.JSeparator jSeparator20;
	private javax.swing.JSeparator jSeparator21;
	private javax.swing.JToolBar.Separator jSeparator22;
	private javax.swing.JSeparator jSeparator3;
	private javax.swing.JSeparator jSeparator4;
	private javax.swing.JToolBar.Separator jSeparator5;
	private javax.swing.JToolBar.Separator jSeparator6;
	private javax.swing.JSeparator jSeparator7;
	private javax.swing.JSeparator jSeparator8;
	private javax.swing.JSeparator jSeparator9;
	private javax.swing.JTabbedPane jTabbedPane1;
	private javax.swing.JToolBar jToolBar1;
	private javax.swing.JTable loadedFileInfoTable;
	private javax.swing.JTable loadedTable;
	private javax.swing.JMenuItem magmaItem;
	private javax.swing.JMenuItem mangoItem;
	private javax.swing.JMenuItem mistAquaItem;
	private javax.swing.JMenuItem mistSilverItem;
	private javax.swing.JMenuItem moderateItem;
	private javax.swing.JMenuItem motifItem;
	private javax.swing.JTable myDownloadTable;
	private ddm.p2p.myfilesharing.view.MySearchTabbedPanel mySearchTabPanel;
	private javax.swing.JTable mySharedTable;
	private javax.swing.JMenuItem nebulaBrickWallItem;
	private javax.swing.JMenuItem nebulaItem;
	private javax.swing.JLabel nicknameField;
	private javax.swing.JMenuItem officeBlue2007Item;
	private javax.swing.JMenuItem officeSilver2007Item;
	private javax.swing.JMenuItem openFileItem;
	private javax.swing.JMenuItem openFolderItem;
	private javax.swing.JMenuItem pauseDownloadItem;
	private javax.swing.JMenuItem ravenGraphiteGlassItem;
	private javax.swing.JMenuItem ravenGraphiteItem;
	private javax.swing.JMenuItem ravenItem;
	private javax.swing.JMenuItem recommandItem;
	private javax.swing.JPopupMenu recommandPop;
	private javax.swing.JTable recommandTable;
	private javax.swing.JMenuItem refreshDownloadedFilesItem;
	private javax.swing.JButton refreshLoadedFiles;
	private javax.swing.JButton refreshUnloadedFiles;
	private javax.swing.JMenuItem saharaItem;
	private javax.swing.JMenuItem sharedFilesAddItem;
	private javax.swing.JMenuItem sharedFilesRereshItem;
	private javax.swing.JMenuItem sharedOpenFileItem;
	private javax.swing.JMenuItem sharedOpenFolderItem;
	private javax.swing.JPopupMenu sharedTabelPop;
	private javax.swing.JMenuItem swingItem;
	private javax.swing.JPopupMenu trayPop;
	public javax.swing.JTextArea txtLog;
	private javax.swing.JTable undownloadedFileInfoTable;
	private javax.swing.JMenuItem unshareFileItem;
	public javax.swing.JList viewer;
	private javax.swing.JMenuItem windowItem;
	// End of variables declaration//GEN-END:variables
}
