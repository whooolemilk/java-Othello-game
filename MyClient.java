import java.net.*;
import java.io.*;
import javax.swing.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

//���y�Đ����ɕK�v
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
		//���O�̓��̓_�C�A���O���J��
		String myName = JOptionPane.showInputDialog(null,"���O����͂��Ă�������","���O�̓���",JOptionPane.QUESTION_MESSAGE);
		if(myName.equals("")){
			myName = "No name";//���O���Ȃ��Ƃ��́C"No name"�Ƃ���
		}

		String myAddress = JOptionPane.showInputDialog(null,"IP�A�h���X����͂��Ă�������","IP�A�h���X�̓���",JOptionPane.QUESTION_MESSAGE);
		if(myAddress.equals("")){
			myAddress = "localhost";//���O���Ȃ��Ƃ��́C"localhoat"�Ƃ���
		}

		//�E�B���h�E���쐬����
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//�E�B���h�E�����Ƃ��ɁC����������悤�ɐݒ肷��
		setTitle("MyClient");//�E�B���h�E�̃^�C�g����ݒ肷��
		setSize(650,550);//�E�B���h�E�̃T�C�Y��ݒ肷��
		c = getContentPane();//�t���[���̃y�C�����擾����
    c.setBackground(Color.WHITE);//�E�B���h�E�̐F�̐ݒ�

		//�A�C�R���̐ݒ�
		whiteIcon = new ImageIcon("White1.jpg");
		blackIcon = new ImageIcon("Black1.jpg");
		boardIcon = new ImageIcon("GreenFrame1.jpg");

		c.setLayout(null);//�������C�A�E�g�̐ݒ���s��Ȃ�
		//�{�^���̐���
    passButton = new JButton();
    c.add(passButton);
    passButton.setText("�p�X");
    passButton.setBounds(500,350,100,50);
    passButton.addMouseListener(this);
    passButton.setActionCommand("PASS");

    resetButton = new JButton();
    c.add(resetButton);
    resetButton.setText("���Z�b�g");
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

    //�T�[�o�ɐڑ�����
		Socket socket = null;
		try {
		  //"localhost"�́C���������ւ̐ڑ��Dlocalhost��ڑ����IP Address�i"133.42.155.201"�`���j�ɐݒ肷��Ƒ���PC�̃T�[�o�ƒʐM�ł���
			//10000�̓|�[�g�ԍ��DIP Address�Őڑ�����PC�����߂āC�|�[�g�ԍ��ł���PC�㓮�삷��v���O��������肷��
			socket = new Socket("localhost", 10000);
		} catch (UnknownHostException e) {
			System.err.println("�z�X�g�� IP �A�h���X������ł��܂���: " + e);
		} catch (IOException e) {
			 System.err.println("�G���[���������܂���: " + e);
		}

		MesgRecvThread mrt = new MesgRecvThread(socket, myName);//��M�p�̃X���b�h���쐬����
		mrt.start();//�X���b�h�𓮂����iRun�������j
	}
		
	//���b�Z�[�W��M�̂��߂̃X���b�h
	public class MesgRecvThread extends Thread {
		
		Socket socket;
		String myName;
		
		public MesgRecvThread(Socket s, String n){
			socket = s;
			myName = n;
		}
		
	  //�ʐM�󋵂��Ď����C��M�f�[�^�ɂ���ē��삷��
		public void run() {
			try{
				InputStreamReader sisr = new InputStreamReader(socket.getInputStream());
				BufferedReader br = new BufferedReader(sisr);
				out = new PrintWriter(socket.getOutputStream(), true);
				out.println(myName);//�ڑ��̍ŏ��ɖ��O�𑗂�
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
          System.out.println("���Ȃ��̔Ԃ���n�܂��I");
        }else{
          System.out.println("����̔Ԃ���n�܂��I");
        }
        
				while(true) {
					String inputLine = br.readLine();
          System.out.println("inputline="+inputLine);
					if (inputLine != null) {
						String[] inputTokens = inputLine.split(" ");
						String cmd = inputTokens[0];
            if(cmd.equals("PASS")){
              myTurn = 1 - myTurn;
              System.out.println("�{�^���p�X����");
              if(myTurn==1){
                  System.out.println("���Ȃ��̔Ԃł�");
                }else{
                  System.out.println("����̔Ԃł�");
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
                    System.out.println("���Ȃ��̔Ԃ���͂��܂��");
                  }else{
                    System.out.println("����̔Ԃ���͂��܂��I");
                }
                System.out.println("���Z�b�g����");
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
                    System.out.println("���Ȃ��̔Ԃł�");
                  }else{
                    System.out.println("����̔Ԃł�");
                  }
                }else{
                  System.out.println("�����p�X����");
                  if(judgeCount2() == 0){
                    System.out.println("�I��");
                  
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
                    System.out.println("���̐�"+whiteIconCount);
                    System.out.println("���̐�"+blackIconCount);
                    if(whiteIconCount==blackIconCount){
                      System.out.println("�Ђ��킯");
                    }else if(whiteIconCount>blackIconCount){
                      if(myIcon == whiteIcon){
                        System.out.println("����");
                      }else{
                        System.out.println("����");
                      }
                    }else{
                      if(myIcon == blackIcon){
                        System.out.println("����");
                      }else{
                        System.out.println("����");
                      }
                    }
                  }
                }
                //System.out.println("MyTurnp2"+myTurn);
              }

              if(cmd.equals("FLIP")){
                String theBName = inputTokens[1];//�{�^���̖��O�i�ԍ��j�̎擾
                int theBnum = Integer.parseInt(theBName);//�{�^���̖��O�𐔒l�ɕϊ�����
                int i = theBnum / 8;
                int j = theBnum % 8;
                int theColor = Integer.parseInt(inputTokens[2]);//���l�ɕϊ�����
                if(theColor==myColor){
                  buttonArray[i][j].setIcon(myIcon);//blackIcon�ɐݒ肷��
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
				System.err.println("�G���[���������܂���: " + e);
			}
		}
	}


	public static void main(String[] args) {
		MyClient net = new MyClient();
		net.setVisible(true);
	}
  	
	public void mouseClicked(MouseEvent e) {
    if(myTurn==1){
      System.out.println("�N���b�N");
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
        //�u����
        System.out.println("���s");
        theSoundPlayer2 = new SoundPlayer("443_2.wav");
        String msg = "PLACE"+" "+theArrayIndex+" "+myColor;
        out.println(msg);
        out.flush();
      } else {
        //�u���Ȃ�
        System.out.println("�����ɂ͔z�u�ł��܂���");
      }
      }
      repaint();//��ʂ̃I�u�W�F�N�g��`�悵����
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

  // �u����Ֆʂ��ǂ����𔻒肷��֐�
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
            //�{�^���̈ʒu�������
            int msgy = y + dy;
            int msgx = x + dx;
            int theArrayIndex = msgy*8 + msgx;
            //�T�[�o�ɏ��𑗂�
            String msg = "FLIP"+" "+theArrayIndex+" "+myColor;
            out.println(msg);
            out.flush();
          }
        }
      }
    }
    return flag;
  }

  // �Ђ�����Ԃ����Ƃ̂ł���Ֆʂ̌���Ԃ��֐�
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

  // ���W��8�~8�̃}�X�ڂ𒴂��Ă��邩�ǂ����𔻒肷��֐�
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

// ���y�Đ�
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
            //clip.setLoopPoints(0,clip.getFrameLength());//�������[�v�ƂȂ�
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
                long time = (long)clip.getFrameLength();//44100�Ŋ���ƍĐ����ԁi�b�j���ł�
                //System.out.println("PlaySound time="+time);
                long endTime = System.currentTimeMillis()+time*1000/44100;
                clip.start();
                //System.out.println("PlaySound time="+(int)(time/44100));
                while(true){
                    if(stopFlag){//stopFlag��true�ɂȂ����I��
                        //System.out.println("PlaySound stop by stopFlag");
                        clip.stop();
                        return;
                    }
                    //System.out.println("endTime="+endTime);
                    //System.out.println("currentTimeMillis="+System.currentTimeMillis());
                    if(endTime < System.currentTimeMillis()){//�Ȃ̒������߂�����I��
                        //System.out.println("PlaySound stop by sound length");
                        if(loopFlag) {
                            clip.loop(1);//�������[�v�ƂȂ�
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
