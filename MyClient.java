import java.net.*;
import java.io.*;
import javax.swing.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import java.io.File;//音楽再生時に必要
import javax.sound.sampled.AudioFormat;//音楽再生時に必要
import javax.sound.sampled.AudioSystem;//音楽再生時に必要
import javax.sound.sampled.Clip;//音楽再生時に必要
import javax.sound.sampled.DataLine;//音楽再生時に必要

public class MyClient extends JFrame implements MouseListener,MouseMotionListener,ActionListener {
	private JButton buttonArray[][];//ボタン用の配列
  private JButton passButton;
  private int myColor;
  private int myTurn;
  private ImageIcon myIcon, yourIcon;
	private Container c;
	private ImageIcon blackIcon, whiteIcon, boardIcon;
	PrintWriter out;//出力用のライター
  SoundPlayer theSoundPlayer2;

	public MyClient() {
		//名前の入力ダイアログを開く
		String myName = JOptionPane.showInputDialog(null,"名前を入力してください","名前の入力",JOptionPane.QUESTION_MESSAGE);
		if(myName.equals("")){
			myName = "No name";//名前がないときは，"No name"とする
		}

		String myAddress = JOptionPane.showInputDialog(null,"IPアドレスを入力してください","IPアドレスの入力",JOptionPane.QUESTION_MESSAGE);
		if(myAddress.equals("")){
			myAddress = "localhost";//名前がないときは，"localhoat"とする
		}

		//ウィンドウを作成する
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//ウィンドウを閉じるときに，正しく閉じるように設定する
		setTitle("MyClient");//ウィンドウのタイトルを設定する
		setSize(500,500);//ウィンドウのサイズを設定する
		c = getContentPane();//フレームのペインを取得する

		//アイコンの設定
		whiteIcon = new ImageIcon("White.jpg");
		blackIcon = new ImageIcon("Black.jpg");
		boardIcon = new ImageIcon("GreenFrame.jpg");

		c.setLayout(null);//自動レイアウトの設定を行わない
		//ボタンの生成
    passButton = new JButton();
    c.add(passButton);
    passButton.setText("パス");
    passButton.setBounds(405,405,60,60);
    passButton.addMouseListener(this);
    passButton.setActionCommand("PASS");

		buttonArray = new JButton[8][8];//ボタンの配列を５個作成する[0]から[4]まで使える
		for(int j=0;j<8;j++){
      for(int i=0; i<8; i++){
			buttonArray[j][i] = new JButton(boardIcon);//ボタンにアイコンを設定する
			c.add(buttonArray[j][i]);//ペインに貼り付ける
			buttonArray[j][i].setBounds(i*50,j*50,50,50);//ボタンの大きさと位置を設定する．(x座標，y座標,xの幅,yの幅）
			buttonArray[j][i].addMouseListener(this);//ボタンをマウスでさわったときに反応するようにする
			//buttonArray[i][j].addMouseMotionListener(this);//ボタンをマウスで動かそうとしたときに反応するようにする
			buttonArray[j][i].setActionCommand(Integer.toString(j*8+i));//ボタンに配列の情報を付加する（ネットワークを介してオブジェクトを識別するため）
      }
		}
    buttonArray[3][3].setIcon(whiteIcon);
    buttonArray[3][4].setIcon(blackIcon);
    buttonArray[4][3].setIcon(blackIcon);
    buttonArray[4][4].setIcon(whiteIcon);

		//サーバに接続する
		Socket socket = null;
		try {
			//"localhost"は，自分内部への接続．localhostを接続先のIP Address（"133.42.155.201"形式）に設定すると他のPCのサーバと通信できる
			//10000はポート番号．IP Addressで接続するPCを決めて，ポート番号でそのPC上動作するプログラムを特定する
			socket = new Socket("localhost", 10000);
		} catch (UnknownHostException e) {
			System.err.println("ホストの IP アドレスが判定できません: " + e);
		} catch (IOException e) {
			 System.err.println("エラーが発生しました: " + e);
		}

		MesgRecvThread mrt = new MesgRecvThread(socket, myName);//受信用のスレッドを作成する
		mrt.start();//スレッドを動かす（Runが動く）
	}
		
	//メッセージ受信のためのスレッド
	public class MesgRecvThread extends Thread {
		
		Socket socket;
		String myName;
		
		public MesgRecvThread(Socket s, String n){
			socket = s;
			myName = n;
		}
		
		//通信状況を監視し，受信データによって動作する
		public void run() {
			try{
				InputStreamReader sisr = new InputStreamReader(socket.getInputStream());
				BufferedReader br = new BufferedReader(sisr);
				out = new PrintWriter(socket.getOutputStream(), true);
				out.println(myName);//接続の最初に名前を送る
        String myNumberStr = br.readLine();
        int myNumberInt = Integer.parseInt(myNumberStr);
        if(myNumberInt % 2 != 0){
          myColor=0;//player1:黒
          myTurn=1;
          myIcon=blackIcon;
          yourIcon=whiteIcon;
        }else{
          myColor=1;//player2:白
          myTurn=0;
          myIcon=whiteIcon;
          yourIcon=blackIcon;
        }
        System.out.println(myTurn);
				while(true) {
					String inputLine = br.readLine();//データを一行分だけ読み込んでみる
					if (inputLine != null) {//読み込んだときにデータが読み込まれたかどうかをチェックする
						System.out.println(inputLine);//デバッグ（動作確認用）にコンソールに出力する
						String[] inputTokens = inputLine.split(" ");	//入力データを解析するために、スペースで切り分ける
						String cmd = inputTokens[0];//コマンドの取り出し．１つ目の要素を取り出す
						/*if(cmd.equals("MOVE")){//cmdの文字と"MOVE"が同じか調べる．同じ時にtrueとなる
							//MOVEの時の処理(コマの移動の処理)
							String theBName = inputTokens[1];//ボタンの名前（番号）の取得
							int theBnum = Integer.parseInt(theBName);//ボタンの名前を数値に変換する
							int x = Integer.parseInt(inputTokens[2]);//数値に変換する
							int y = Integer.parseInt(inputTokens[3]);//数値に変換する
							buttonArray[theBnum].setLocation(x,y);//指定のボタンを位置をx,yに設定する
						}*/
              if(cmd.equals("PASS")){
                myTurn = 1 - myTurn;
              }
              if(cmd.equals("PLACE")){
                myTurn = 1 - myTurn;
                String theBName = inputTokens[1];//ボタンの名前（番号）の取得
                int theBnum = Integer.parseInt(theBName);//ボタンの名前を数値に変換する
                int i = theBnum / 8;//(タテ、ヨコ)=(2, 3)のとき、タテ×8+ヨコより、19番目のボタン。
                int j = theBnum % 8;// したがって、iはヨコ、jはタテを表す
                int theColor = Integer.parseInt(inputTokens[2]);//数値に変換する
                if(theColor==myColor){
                  buttonArray[i][j].setIcon(myIcon);//blackIconに設定する
                }else{
                  buttonArray[i][j].setIcon(yourIcon);
                }

                int boardIconCount = 0;
                int whiteIconCount = 0;
                int blackIconCount = 0;
                for(int ia = 0; ia <8; ia++){
                  for(int ja = 0; ja < 8; ja++){
                    Icon theIcon = buttonArray[ia][ja].getIcon();
                    if(theIcon == boardIcon){
                      boardIconCount ++;
                    }else if(theIcon == whiteIcon){
                      whiteIconCount ++;
                    }else if(theIcon == blackIcon){
                      blackIconCount ++;
                    }
                  }
                }
                if(boardIconCount==0 || (whiteIconCount==0&&blackIconCount>0) || (whiteIconCount>1&&blackIconCount==0)){
                  System.out.println("白の数"+whiteIconCount);
                  System.out.println("黒の数"+blackIconCount);
                  if(whiteIconCount==0 && myIcon == whiteIcon || blackIconCount==0 && myIcon == blackIcon){
                     System.out.println("負け");
                  }else if(whiteIconCount>0 && myIcon == whiteIcon || blackIconCount>0 && myIcon == blackIcon){
                     System.out.println("勝ち");
                  }
                }
              }
              if(cmd.equals("FLIP")){
                String theBName = inputTokens[1];//ボタンの名前（番号）の取得
                int theBnum = Integer.parseInt(theBName);//ボタンの名前を数値に変換する
                int i = theBnum / 8;
                int j = theBnum % 8;
                int theColor = Integer.parseInt(inputTokens[2]);//数値に変換する
                if(theColor==myColor){
                  buttonArray[i][j].setIcon(myIcon);//blackIconに設定する
                }else{
                  buttonArray[i][j].setIcon(yourIcon);
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
  	
	public void mouseClicked(MouseEvent e) {//ボタンをクリックしたときの処理
    
    if(myTurn==1){
      System.out.println("クリック");
      JButton theButton = (JButton)e.getComponent();//クリックしたオブジェクトを得る．型が違うのでキャストする
      String theArrayIndex = theButton.getActionCommand();//ボタンの配列の番号を取り出す
      Icon theIcon = theButton.getIcon();//theIconには，現在のボタンに設定されたアイコンが入る

      if(theArrayIndex.equals("PASS")){
        String msg = "PASS";
        out.println(msg);//送信データをフラッシュ（ネットワーク上にはき出す）する
        out.flush();
      }

      if(theIcon == boardIcon){
      int temp = Integer.parseInt(theArrayIndex);
      System.out.println(temp);
      int x = temp % 8;
      int y = temp / 8;
      System.out.println(x);
      System.out.println(y);
      if(judgeButton(y, x)){
        //置ける
        String msg = "PLACE"+" "+theArrayIndex+" "+myColor;
        out.println(msg);//送信データをフラッシュ（ネットワーク上にはき出す）する
        out.flush();
      } else {
        //置けない
        System.out.println("そこには配置できません");
      }
      }
      repaint();//画面のオブジェクトを描画し直す
    }
	}
	
	public void mouseEntered(MouseEvent e) {//マウスがオブジェクトに入ったときの処理
		/*System.out.println("マウスが入った");*/
	}
	
	public void mouseExited(MouseEvent e) {//マウスがオブジェクトから出たときの処理
		/*System.out.println("マウス脱出");*/
	}
	
	public void mousePressed(MouseEvent e) {//マウスでオブジェクトを押したときの処理（クリックとの違いに注意）
		/*System.out.println("マウスを押した");*/
	}
	
	public void mouseReleased(MouseEvent e) {//マウスで押していたオブジェクトを離したときの処理
		/*System.out.println("マウスを放した");*/
	}
	
	public void mouseDragged(MouseEvent e) {//マウスでオブジェクトとをドラッグしているときの処理
		/* System.out.println("マウスをドラッグ");
		JButton theButton = (JButton)e.getComponent();//型が違うのでキャストする
		String theArrayIndex = theButton.getActionCommand();//ボタンの配列の番号を取り出す
    if(!theArrayIndex.equals("0")){
      Point theMLoc = e.getPoint();//発生元コンポーネントを基準とする相対座標
      System.out.println(theMLoc);//デバッグ（確認用）に，取得したマウスの位置をコンソールに出力する
      Point theBtnLocation = theButton.getLocation();//クリックしたボタンを座標を取得する
      theBtnLocation.x += theMLoc.x-15;//ボタンの真ん中当たりにマウスカーソルがくるように補正する
      theBtnLocation.y += theMLoc.y-15;//ボタンの真ん中当たりにマウスカーソルがくるように補正する
  
      //送信情報を作成する（受信時には，この送った順番にデータを取り出す．スペースがデータの区切りとなる）
      String msg = "MOVE"+" "+theArrayIndex+" "+theBtnLocation.x+" "+theBtnLocation.y;

      //サーバに情報を送る
      out.println(msg);//送信データをバッファに書き出す
      out.flush();//送信データをフラッシュ（ネットワーク上にはき出す）する

      repaint();//オブジェクトの再描画を行う
    }*/
	}

	public void mouseMoved(MouseEvent e) {//マウスがオブジェクト上で移動したときの処理
		/*System.out.println("マウス移動");
		int theMLocX = e.getX();//マウスのx座標を得る
		int theMLocY = e.getY();//マウスのy座標を得る
		System.out.println(theMLocX+","+theMLocY);//コンソールに出力する
    */
	}

  public void actionPerformed(ActionEvent e) {
        System.out.println("アクション発生");
        System.out.println(e.getSource());
        String theCmd = e.getActionCommand();
        System.out.println("ActionCommand: "+theCmd);
        Object theObj = e.getSource();
        System.out.println("クラス名＝" + theObj.getClass().getName());
        String theClass = theObj.getClass().getName();//クラス名を使って動きを変える

        theSoundPlayer2 = new SoundPlayer("443_2.wav");
        theSoundPlayer2.play();

  }

  // 置ける盤面かどうかを判定する関数
  public boolean judgeButton(int y, int x) {
    boolean flag = false;
    for (int j=-1;j<2;j++){
      for (int i=-1;i<2;i++){
        int posY = y + j;
        int posX = x + i;
        //System.out.println(posX);
        //System.out.println(posY);
        if(isExceededArea(posY, posX)){
          continue;
        }

        Icon theIcon = buttonArray[posY][posX].getIcon();
        //System.out.println("y+j="+(y+j)+", x+i="+(x+i));
        int flipNum = flipButtons(y, x, j, i);
        if(flipNum >= 1){
          flag=true;
          for(int dy=j, dx=i, k=0; k<flipNum; k++, dy+=j, dx+=i){
            //ボタンの位置情報を作る
            int msgy = y + dy;
            int msgx = x + dx;
            int theArrayIndex = msgy*8 + msgx;
            //サーバに情報を送る
            String msg = "FLIP"+" "+theArrayIndex+" "+myColor;
            out.println(msg);
            out.flush();
          }
        }
      }
    }
    return flag;
  }

  // ひっくり返すことのできる盤面の個数を返す関数
  public int flipButtons(int y, int x, int j, int i){
    int flipNum = 0;
    for(int dy=j, dx=i; ; dy+=j, dx+=i) {
      int posY = y + dy;
      int posX = x + dx;

      if(isExceededArea(posY, posX)){
        continue;
      }

      Icon theIcon = buttonArray[posY][posX].getIcon();

      if(theIcon == boardIcon){
        flipNum = 0;
        break;
      }else if(theIcon == myIcon){
        break;
      }else if (theIcon == yourIcon){
        flipNum++;
      }
    }
    return flipNum;
  }

  // 座標が8×8のマス目を超えているかどうかを判定する関数
  public boolean isExceededArea(int posY, int posX){
    return posX < 0 || posY < 0 || posX > 7 || posY > 7;
  }

  public int generatePos(int theArrayIndex){
    int posX = theArrayIndex % 8;
    int posY = theArrayIndex / 8;
    return posX;
  }

  // 置ける盤面かどうかを判定する関数
  public boolean autoPass() {
    boolean flag = false;
    for (int j=0;j<8;j++){
      for (int i=0;i<8;i++){
        Icon theIcon = buttonArray[j][i].getIcon();
        if(theIcon==boardIcon){
          if(judgeButton(j,i)){
            flag=true;
          }
        }
      }
    }
    if(flag==true){
      flag=false;
    }else{
      flag=true;
    }
    return flag;
  }


public class SoundPlayer{
    private AudioFormat format = null;
    private DataLine.Info info = null;
    private Clip clip = null;
    boolean stopFlag = false;
    Thread soundThread = null;
    private boolean loopFlag = false;

    public SoundPlayer(String pathname){
        File file = new File(pathname);
        try{
            format = AudioSystem.getAudioFileFormat(file).getFormat();
            info = new DataLine.Info(Clip.class, format);
            clip = (Clip) AudioSystem.getLine(info);
            clip.open(AudioSystem.getAudioInputStream(file));
            //clip.setLoopPoints(0,clip.getFrameLength());//無限ループとなる
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void SetLoop(boolean flag){
        loopFlag = flag;
    }

    public void play(){
        soundThread = new Thread(){
            public void run(){
                long time = (long)clip.getFrameLength();//44100で割ると再生時間（秒）がでる
                System.out.println("PlaySound time="+time);
                long endTime = System.currentTimeMillis()+time*1000/44100;
                clip.start();
                System.out.println("PlaySound time="+(int)(time/44100));
                while(true){
                    if(stopFlag){//stopFlagがtrueになった終了
                        System.out.println("PlaySound stop by stopFlag");
                        clip.stop();
                        return;
                    }
                    System.out.println("endTime="+endTime);
                    System.out.println("currentTimeMillis="+System.currentTimeMillis());
                    if(endTime < System.currentTimeMillis()){//曲の長さを過ぎたら終了
                        System.out.println("PlaySound stop by sound length");
                        if(loopFlag) {
                            clip.loop(1);//無限ループとなる
                        } else {
                            clip.stop();
                            return;
                        }
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        soundThread.start();
    }

    public void stop(){
        stopFlag = true;
        System.out.println("StopSound");
    }

}


}
