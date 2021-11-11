import java.net.*;
import java.io.*;
import javax.swing.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class MyClient extends JFrame implements MouseListener,MouseMotionListener {
	private JButton buttonArray[][];//ボタン用の配�?
    private JButton passbtn;
	private Container c;
	private ImageIcon blackIcon, whiteIcon, boardIcon;
    private ImageIcon myIcon, yourIcon;
    private int myColor, myTurn;
	PrintWriter out;//出力用のライター

	public MyClient() {
		//名前の入力ダイアログを開�?
		String myName = JOptionPane.showInputDialog(null,"IPアドレスを�?力してください","IPアドレスの入�?",JOptionPane.QUESTION_MESSAGE);
		if(myName.equals("")){
			myName = "127.0.0.1";//名前がな�?��き�???"127.0.0.1"とする
		}

		//ウィンドウを作�?する
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//ウィンドウを閉じるときに?�正しく閉じるよ�?��設定す�?
		setTitle("MyClient");//ウィンドウのタイトルを設定す�?
		setSize(500,500);//ウィンドウのサイズを設定す�?
		c = getContentPane();//フレー�?のペインを取得す�?

		//アイコンの設�?
		whiteIcon = new ImageIcon("White.jpg");
		blackIcon = new ImageIcon("Black.jpg");
		boardIcon = new ImageIcon("GreenFrame.jpg");

		c.setLayout(null);//自動レイアウト�?設定を行わな�?
		//ボタンの生�?
		buttonArray = new JButton[8][8];//ボタンの配�?を５個作�?する[0]から[4]まで使える
		for(int j=0;j<8;j++){
            for(int i=0;i<8;i++){
                buttonArray[j][i] = new JButton(boardIcon);//ボタンにアイコンを設定す�?
                c.add(buttonArray[j][i]);//ペインに貼り付け�?
                buttonArray[j][i].setBounds(i*45,j*45+10,45,45);//ボタンの大きさと位置を設定する�?(x座標，y座�?,xの�?,yの�?�?
                buttonArray[j][i].addMouseListener(this);//ボタンを�?ウスでさわったときに反応するよ�?��する
                buttonArray[j][i].addMouseMotionListener(this);//ボタンを�?ウスで動かそうとしたときに反応するよ�?��する
                buttonArray[j][i].setActionCommand(Integer.toString(j*8+i));//ボタンに配�?の�??�を付加する?�ネ�?��ワークを介してオブジェクトを識別するため??
            }
		}
        buttonArray[3][3].setIcon(whiteIcon);
        buttonArray[3][4].setIcon(blackIcon);
        buttonArray[4][3].setIcon(blackIcon);
        buttonArray[4][4].setIcon(whiteIcon);
		
        
		//サーバに接続す�?
		Socket socket = null;
		try {
			//"localhost"は?��?�??部への接続．localhostを接続�?のIP Address??"133.42.155.201"形式）に設定すると他�?PCのサーバと通信できる
			//10000はポ�?ト番号?�IP Addressで接続するPCを決めて?��?ート番号でそ�?PC上動作する�?ログラ�?を特定す�?
			socket = new Socket("localhost", 10000);
		} catch (UnknownHostException e) {
			System.err.println("ホスト�? IP アドレスが判定できません: " + e);
		} catch (IOException e) {
			 System.err.println("エラーが発生しました: " + e);
		}
		
		MesgRecvThread mrt = new MesgRecvThread(socket, myName);//受信用のスレ�?��を作�?する
		mrt.start();//スレ�?��を動かす??Runが動く�?
	}
		
	//メ�?��ージ受信のためのスレ�?��
	public class MesgRecvThread extends Thread {
		
		Socket socket;
		String myName;
		
		public MesgRecvThread(Socket s, String n){
			socket = s;
			myName = n;
		}
		
		//通信状況を監視し?�受信�??タによって動作す�?
		public void run() {
			try{
				InputStreamReader sisr = new InputStreamReader(socket.getInputStream());
				BufferedReader br = new BufferedReader(sisr);
				out = new PrintWriter(socket.getOutputStream(), true);
				out.println(myName);//接続�?最初に名前を送る
                String myNumberStr = br.readLine();
                int myNumberInt = Integer.parseInt(myNumberStr);
                if(myNumberInt % 2 == 0){
                    myColor = 0; //myColorを黒に設�?
                    myTurn = 0;
                    myIcon = blackIcon;
                    yourIcon = whiteIcon;
                }else{
                    myColor = 1; //myColorを白に設�?
                    myTurn = 1;
                    myIcon = whiteIcon;
                    yourIcon = blackIcon;
                }
				while(true) {
					String inputLine = br.readLine();//�??タを一行�?�?け読み込んでみ�?
					if (inputLine != null) {//読み込んだときに�??タが読み込まれたかど�?��をチェ�?��する
						System.out.println(inputLine);//�?���?��?�動作確認用?�にコンソールに出力す�?
						String[] inputTokens = inputLine.split(" ");	//入力データを解析するために、スペ�?スで�?���?���?
						String cmd = inputTokens[0];//コマンド�?取り出し．１つ目の要�?を取り�?�?
						if(cmd.equals("MOVE")){//cmdの�?��と"MOVE"が同じか調べる．同じ時にtrueとな�?
							//MOVEの時�?処�?(コマ�?移動�?処�?)
							// String theBName = inputTokens[1];//ボタンの名前?�番号?��?取�?
							// int theBnum = Integer.parseInt(theBName);//ボタンの名前を数値に変換する
							// int x = Integer.parseInt(inputTokens[2]);//数値に変換する
							// int y = Integer.parseInt(inputTokens[3]);//数値に変換する
							// buttonArray[theBnum].setLocation(x,y);//�?���?ボタンを位置をx,yに設定す�?
						}else if(cmd.equals("PLACE")){
                            int theBnum = Integer.parseInt(inputTokens[1]);
                            int theColor = Integer.parseInt(inputTokens[2]);
                            int y = theBnum / 8;
                            int x = theBnum % 8;
                            if (theColor == myColor) {
                                buttonArray[y][x].setIcon(myIcon);//blackIconに設定す�?
                            }else{
                                buttonArray[y][x].setIcon(yourIcon);//whiteIconに設定す�?
                            }
                            myTurn = 1 - myTurn;
                            if (judgeCount() == 0){
                                myTurn = 1 - myTurn;
                            }
                        }else if (cmd.equals("FLIP")) {
                            int theBnum = Integer.parseInt(inputTokens[1]);
                            int theColor = Integer.parseInt(inputTokens[2]);
                            int y = theBnum / 8;
                            int x = theBnum % 8;
                            if (theColor == myColor) {
                                buttonArray[y][x].setIcon(myIcon);//blackIconに設定す�?
                            }else{
                                buttonArray[y][x].setIcon(yourIcon);//whiteIconに設定す�?
                            }
                        }
					}else{
						break;
					}
				
				}
				socket.close();
			} catch (IOException e) {
				System.err.println("エラーが発生しました: " + e);
			}
		}
	}

	public static void main(String[] args) {
		MyClient net = new MyClient();
		net.setVisible(true);
	}
  	
	public void mouseClicked(MouseEvent e) {//ボタンをクリ�?��したとき�?処�?
		System.out.println("クリ�?��");
		JButton theButton = (JButton)e.getComponent();//クリ�?��したオブジェクトを得る?�型が違�??でキャストす�?
		String theArrayIndex = theButton.getActionCommand();//ボタンの配�?の番号を取り�?�?

		Icon theIcon = theButton.getIcon();//theIconには?�現在のボタンに設定されたアイコンが�?�?
		System.out.println(theIcon);//�?���?��?�確認用?�に?�クリ�?��したアイコンの名前を�?力す�?
         
        if ((myTurn == 0) && (theIcon == boardIcon)) {
            int temp = Integer.parseInt(theArrayIndex);
            int y = temp / 8;
            int x = temp % 8;
            if(judgeButton(y, x)){//置ける
                //送信�??�を作�?する?�受信時には?�この送った�??��に�??タを取り�?す．スペ�?スがデータの区�?��となる�?
                String msg = "PLACE"+" "+theArrayIndex+" "+ myColor;
                //サーバに�??�を送る
                out.println(msg);//送信�??タをバ�?��ァに書き�?�?
            } else {//置けな�?
              System.out.println("そこには配置できません");
            }
            out.flush();//送信�??タをフラ�?��ュ?�ネ�?��ワーク上にはき�?す）す�?
            repaint();//画面のオブジェクトを描画し直�?
        }
	}
	
	public void mouseEntered(MouseEvent e) {//マウスがオブジェクトに入ったとき�?処�?
		//System.out.println("マウスが�?っ�?");
	}
	
	public void mouseExited(MouseEvent e) {//マウスがオブジェクトから�?たとき�?処�?
		//System.out.println("マウス脱出");
	}
	
	public void mousePressed(MouseEvent e) {//マウスでオブジェクトを押したとき�?処�?��クリ�?��との違いに注意�?
		//System.out.println("マウスを押した");
	}
	
	public void mouseReleased(MouseEvent e) {//マウスで押して�?��オブジェクトを離したとき�?処�?
		//System.out.println("マウスを放した");
	}
	
	public void mouseDragged(MouseEvent e) {//マウスでオブジェクトとをドラ�?��して�?��とき�?処�?
		// System.out.println("マウスをドラ�?��");
		// JButton theButton = (JButton)e.getComponent();//型が違うのでキャストす�?
		// String theArrayIndex = theButton.getActionCommand();//ボタンの配�?の番号を取り�?�?

		// Point theMLoc = e.getPoint();//発生�?コンポ�?ネントを基準とする相対座�?
		// System.out.println(theMLoc);//�?���?��?�確認用?�に?�取得した�?ウスの位置をコンソールに出力す�?
		// Point theBtnLocation = theButton.getLocation();//クリ�?��したボタンを座標を取得す�?
        // if (!(theArrayIndex.equals("1"))){
            // theBtnLocation.x += theMLoc.x-15;//ボタンの真ん中当たりにマウスカーソルがくるよ�?��補正する
            // theBtnLocation.y += theMLoc.y-15;//ボタンの真ん中当たりにマウスカーソルがくるよ�?��補正する
        
            // theButton.setLocation(theBtnLocation);//マウスの位置にあわせてオブジェクトを移動す�?
        // }
 
		// //送信�??�を作�?する?�受信時には?�この送った�??��に�??タを取り�?す．スペ�?スがデータの区�?��となる�?
		// String msg = "MOVE"+" "+theArrayIndex+" "+theBtnLocation.x+" "+theBtnLocation.y;

		// //サーバに�??�を送る
		// out.println(msg);//送信�??タをバ�?��ァに書き�?�?
		// out.flush();//送信�??タをフラ�?��ュ?�ネ�?��ワーク上にはき�?す）す�?

		// repaint();//オブジェクト�?再描画を行う
	}

	public void mouseMoved(MouseEvent e) {//マウスがオブジェクト上で移動したとき�?処�?
		// System.out.println("マウス移�?");
		// int theMLocX = e.getX();//マウスのx座標を得る
		// int theMLocY = e.getY();//マウスのy座標を得る
		// System.out.println(theMLocX+","+theMLocY);//コンソールに出力す�?
	}
    
    public boolean judgeButton(int y, int x){
        boolean flag = false;
        //色�?��条件からflagをtrueにするか判断する
        for (int j=-1; j<=1; j++){
            for (int i=-1; i<=1; i++){
                int flipNum = flipButtons(y, x, j, i);
                //if (theIcon == yourIcon){
                if (flipNum >= 1){
                    flag = true;
                    for(int dy=j, dx=i, k=0; k<flipNum; k++, dy+=j, dx+=i){
                      //ボタンの位置�??�を作る
                      int msgy = y + dy;
                      int msgx = x + dx;
                      int theArrayIndex = msgy*8 + msgx;
                      
                      //サーバに�??�を送る
                      String msg = "FLIP"+" "+theArrayIndex+" "+myColor;
                      out.println(msg);
                      out.flush();
                    }
                }
            }
        }
        return flag;
    }
    public int flipButtons(int y, int x, int j, int i){
        int flipNum = 0;

        for(int dy=j, dx=i; ; dy+=j, dx+=i) { 
            if ((y + dy < 0) || (7 < y + dy) || (x + dx < 0) || (7 < x + dx)){
                return 0;
            }
            Icon theIcon = buttonArray[y + dy][x + dx].getIcon();
            if(theIcon == boardIcon){
                return 0;
            } else if (theIcon == myIcon) {
                break;
            } else {
                flipNum++;
            }
        }
        return flipNum;
    }
    
    public boolean passJudge(int y, int x){
        boolean flag = false;
        //色�?��条件からflagをtrueにするか判断する
        for (int j=-1; j<=1; j++){
            for (int i=-1; i<=1; i++){
                int flipNum = flipButtons(y, x, j, i);
                if (flipNum >= 1){
                    flag = true;
                }
            }
        }
        return flag;
    }

    public int judgeCount() {
        int count = 0;
        for (int j = 0; j < 8; j++) {
            for (int i = 0; i < 8; i++) {
                if (passJudge(j, i)) {
                    count++;
                }
            }
        }
        return count;
    }
    
    public void winJudge(){
        int blackNum = 0;
        int whiteNum = 64 - blackNum;
        for (int j = 0; j < 8; j++) {
            for (int i = 0; i < 8; i++) {
                Icon theIcon = buttonArray[j][i].getIcon();
                if (theIcon == blackIcon) {
                    blackIcon++;
                }
            }
        }
        if (blackNum > whiteNum){
            System.out.println("黒�?勝ち");
        }　else if(blackNum < whiteNum) {
            System.out.println("白の勝ち");
        } else {
            System.out.println("引き�?��");
        }
    }
}