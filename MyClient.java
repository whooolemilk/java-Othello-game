import java.net.*;
import java.io.*;
import javax.swing.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

//音楽再生時に必要
import java.io.File;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

public class MyClient extends JFrame implements MouseListener,MouseMotionListener {
	private JButton buttonArray[][];
  private JButton passButton;
  private JButton resetButton;
  private int myColor;
  private int myTurn;
  private ImageIcon myIcon, yourIcon;
	private Container c;
	private ImageIcon blackIcon, whiteIcon, boardIcon;
	PrintWriter out;

  
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
		setSize(650,550);//ウィンドウのサイズを設定する
		c = getContentPane();//フレームのペインを取得する
    c.setBackground(Color.WHITE);//ウィンドウの色の設定

		//アイコンの設定
		whiteIcon = new ImageIcon("White1.jpg");
		blackIcon = new ImageIcon("Black1.jpg");
		boardIcon = new ImageIcon("GreenFrame1.jpg");

		c.setLayout(null);//自動レイアウトの設定を行わない
		//ボタンの生成
    passButton = new JButton();
    c.add(passButton);
    passButton.setText("パス");
    passButton.setBounds(500,350,100,50);
    passButton.addMouseListener(this);
    passButton.setActionCommand("PASS");

    resetButton = new JButton();
    c.add(resetButton);
    resetButton.setText("リセット");
    resetButton.setBounds(500,400,100,50);
    resetButton.addMouseListener(this);
    resetButton.setActionCommand("RESET");

		buttonArray = new JButton[8][8];
		for(int j=0;j<8;j++){
      for(int i=0; i<8; i++){
			buttonArray[j][i] = new JButton(boardIcon);
			c.add(buttonArray[j][i]);
			buttonArray[j][i].setBounds(i*50+50,j*50+50,50,50);
			buttonArray[j][i].addMouseListener(this);
			//buttonArray[i][j].addMouseMotionListener(this);
			buttonArray[j][i].setActionCommand(Integer.toString(j*8+i));
      //buttonArray[j][i].addActionListener(this);
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
          myColor=0;//player1:Black
          myTurn=1;
          myIcon=blackIcon;
          yourIcon=whiteIcon;
        }else{
          myColor=1;//player2:White
          myTurn=0;
          myIcon=whiteIcon;
          yourIcon=blackIcon;
        }
        if(myTurn==1){
          System.out.println("あなたの番から始まるよ！");
        }else{
          System.out.println("相手の番から始まるよ！");
        }
        
				while(true) {
					String inputLine = br.readLine();
          System.out.println("inputline="+inputLine);
					if (inputLine != null) {
						String[] inputTokens = inputLine.split(" ");
						String cmd = inputTokens[0];
            if(cmd.equals("PASS")){
              myTurn = 1 - myTurn;
              System.out.println("ボタンパス成功");
              if(myTurn==1){
                  System.out.println("あなたの番です");
                }else{
                  System.out.println("相手の番です");
              }
            }
              if(cmd.equals("RESET")){
                for(int j=0;j<8;j++){
                  for(int i=0; i<8; i++){
                  buttonArray[j][i].setIcon(boardIcon);
                  }
                }
                buttonArray[3][3].setIcon(whiteIcon);
                buttonArray[3][4].setIcon(blackIcon);
                buttonArray[4][3].setIcon(blackIcon);
                buttonArray[4][4].setIcon(whiteIcon); 
                if(myTurn==1){
                    System.out.println("あなたの番からはじまるよ");
                  }else{
                    System.out.println("相手の番からはじまるよ！");
                }
                System.out.println("リセット成功");
              }
              if(cmd.equals("PLACE")){
                String theBName = inputTokens[1];
                int theBnum = Integer.parseInt(theBName);
                int i = theBnum / 8;
                int j = theBnum % 8;
                int theColor = Integer.parseInt(inputTokens[2]);
                
                if(theColor==myColor){
                  buttonArray[i][j].setIcon(myIcon);
                  theSoundPlayer2.play();
                }else{
                  buttonArray[i][j].setIcon(yourIcon);
                }
                if (judgeCount() != 0){
                  myTurn = 1 - myTurn;
                  if(myTurn==1){
                    System.out.println("あなたの番です");
                  }else{
                    System.out.println("相手の番です");
                  }
                }else{
                  System.out.println("自動パス成功");
                  if(judgeCount2() == 0){
                    System.out.println("終了");
                  
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
                    System.out.println("白の数"+whiteIconCount);
                    System.out.println("黒の数"+blackIconCount);
                    if(whiteIconCount==blackIconCount){
                      System.out.println("ひきわけ");
                    }else if(whiteIconCount>blackIconCount){
                      if(myIcon == whiteIcon){
                        System.out.println("勝ち");
                      }else{
                        System.out.println("負け");
                      }
                    }else{
                      if(myIcon == blackIcon){
                        System.out.println("勝ち");
                      }else{
                        System.out.println("負け");
                      }
                    }
                  }
                }
                //System.out.println("MyTurnp2"+myTurn);
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
  	
	public void mouseClicked(MouseEvent e) {
    if(myTurn==1){
      System.out.println("クリック");
      JButton theButton = (JButton)e.getComponent();
      String theArrayIndex = theButton.getActionCommand();
      Icon theIcon = theButton.getIcon();

     

      if(theArrayIndex.equals("PASS")){
        String msg = "PASS";
        out.println(msg);
        out.flush();
      }

      if(theArrayIndex.equals("RESET")){
        String msg = "RESET";
        out.println(msg);
        out.flush();
      }      

      if(theIcon == boardIcon){
      int temp = Integer.parseInt(theArrayIndex);
      System.out.println("theArrayIndex="+temp);
      int x = temp % 8;
      int y = temp / 8;

      System.out.println("judgeButton="+judgeButton(y, x));
      if(judgeButton(y, x)){
        //置ける
        System.out.println("実行");
        theSoundPlayer2 = new SoundPlayer("443_2.wav");
        String msg = "PLACE"+" "+theArrayIndex+" "+myColor;
        out.println(msg);
        out.flush();
      } else {
        //置けない
        System.out.println("そこには配置できません");
      }
      }
      repaint();//画面のオブジェクトを描画し直す
    }
	}
	
	public void mouseEntered(MouseEvent e) {
	}
	
	public void mouseExited(MouseEvent e) {
	}
	
	public void mousePressed(MouseEvent e) {
	}
	
	public void mouseReleased(MouseEvent e) {
	}
	
	public void mouseDragged(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}

  // 置ける盤面かどうかを判定する関数
  public boolean judgeButton(int y, int x) {
    boolean flag = false;
    for (int j=-1;j<2;j++){
      for (int i=-1;i<2;i++){
        int posY = y + j;
        int posX = x + i;
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
    if ( (j==0) && (i==0) ) return 0;
    for(int dy=j, dx=i; ; dy+=j, dx+=i) {
      int posY = y + dy;
      int posX = x + dx;

      if(isExceededArea(posY, posX)){
        return 0;
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

  // 
  public boolean passJudge(int y, int x) {
    boolean flag = false;
    int flipNum = 0;
    for (int j=-1; j<=1; j++){
        for (int i=-1; i<=1; i++){
            if ( (j==0) && (i==0) ) continue;
            if(myTurn==1){
              flipNum = Buttons(y, x, j, i);
            }else{
              flipNum = flipButtons(y, x, j, i);
            }
            
            if (flipNum >= 1){
                flag = true;
                break;
            }
        }
    }
    return flag;
  }

    public boolean passJudge2(int y, int x) {
    boolean flag = false;
    int flipNum = 0;
    for (int j=-1; j<=1; j++){
        for (int i=-1; i<=1; i++){
            if ( (j==0) && (i==0) ) continue;
            if(myTurn==1){
              flipNum = flipButtons(y, x, j, i);
            }else{
              
              flipNum = Buttons(y, x, j, i);
            }
            if (flipNum >= 1){
                flag = true;
                break;
            }
        }
    }
    return flag;
  }

    public int Buttons(int y, int x, int j, int i){
    int flipNum = 0;
    if ( (j==0) && (i==0) ) return 0;
    for(int dy=j, dx=i; ; dy+=j, dx+=i) {
      int posY = y + dy;
      int posX = x + dx;

      if(isExceededArea(posY, posX)){
        return 0;
      }

      Icon theIcon = buttonArray[posY][posX].getIcon();

      if(theIcon == boardIcon){
        flipNum = 0;
        break;
      }else if(theIcon == yourIcon){
        break;
      }else if (theIcon == myIcon){
        flipNum++;
      }
    }
    return flipNum;
  }


  public int judgeCount2(){
    int count = 0;
    for (int j = 0; j < 8; j++) {
        for (int i = 0; i < 8; i++) {
          Icon theIcon = buttonArray[j][i].getIcon();

          if((theIcon == boardIcon) && passJudge2(j, i)) {
                count++;
            }
        }
    }
    System.out.println("judgeCount="+count);
    return count;
  }

  
  public int judgeCount(){
    int count = 0;
    for (int j = 0; j < 8; j++) {
        for (int i = 0; i < 8; i++) {
          Icon theIcon = buttonArray[j][i].getIcon();

          if((theIcon == boardIcon) && passJudge(j, i)) {
                count++;
            }
        }
    }
    System.out.println("judgeCount="+count);
    return count;
  }

// 音楽再生
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
                //System.out.println("PlaySound time="+time);
                long endTime = System.currentTimeMillis()+time*1000/44100;
                clip.start();
                //System.out.println("PlaySound time="+(int)(time/44100));
                while(true){
                    if(stopFlag){//stopFlagがtrueになった終了
                        //System.out.println("PlaySound stop by stopFlag");
                        clip.stop();
                        return;
                    }
                    //System.out.println("endTime="+endTime);
                    //System.out.println("currentTimeMillis="+System.currentTimeMillis());
                    if(endTime < System.currentTimeMillis()){//曲の長さを過ぎたら終了
                        //System.out.println("PlaySound stop by sound length");
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
        //System.out.println("StopSound");
    }

}

}
