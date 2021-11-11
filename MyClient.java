import java.net.*;
import java.io.*;
import javax.swing.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import java.io.File;//���y�Đ����ɕK�v
import javax.sound.sampled.AudioFormat;//���y�Đ����ɕK�v
import javax.sound.sampled.AudioSystem;//���y�Đ����ɕK�v
import javax.sound.sampled.Clip;//���y�Đ����ɕK�v
import javax.sound.sampled.DataLine;//���y�Đ����ɕK�v

public class MyClient extends JFrame implements MouseListener,MouseMotionListener,ActionListener {
	private JButton buttonArray[][];//�{�^���p�̔z��
  private JButton passButton;
  private int myColor;
  private int myTurn;
  private ImageIcon myIcon, yourIcon;
	private Container c;
	private ImageIcon blackIcon, whiteIcon, boardIcon;
	PrintWriter out;//�o�͗p�̃��C�^�[
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
		setSize(500,500);//�E�B���h�E�̃T�C�Y��ݒ肷��
		c = getContentPane();//�t���[���̃y�C�����擾����

		//�A�C�R���̐ݒ�
		whiteIcon = new ImageIcon("White.jpg");
		blackIcon = new ImageIcon("Black.jpg");
		boardIcon = new ImageIcon("GreenFrame.jpg");

		c.setLayout(null);//�������C�A�E�g�̐ݒ���s��Ȃ�
		//�{�^���̐���
    passButton = new JButton();
    c.add(passButton);
    passButton.setText("�p�X");
    passButton.setBounds(405,405,60,60);
    passButton.addMouseListener(this);
    passButton.setActionCommand("PASS");

		buttonArray = new JButton[8][8];//�{�^���̔z����T�쐬����[0]����[4]�܂Ŏg����
		for(int j=0;j<8;j++){
      for(int i=0; i<8; i++){
			buttonArray[j][i] = new JButton(boardIcon);//�{�^���ɃA�C�R����ݒ肷��
			c.add(buttonArray[j][i]);//�y�C���ɓ\��t����
			buttonArray[j][i].setBounds(i*50,j*50,50,50);//�{�^���̑傫���ƈʒu��ݒ肷��D(x���W�Cy���W,x�̕�,y�̕��j
			buttonArray[j][i].addMouseListener(this);//�{�^�����}�E�X�ł�������Ƃ��ɔ�������悤�ɂ���
			//buttonArray[i][j].addMouseMotionListener(this);//�{�^�����}�E�X�œ��������Ƃ����Ƃ��ɔ�������悤�ɂ���
			buttonArray[j][i].setActionCommand(Integer.toString(j*8+i));//�{�^���ɔz��̏���t������i�l�b�g���[�N����ăI�u�W�F�N�g�����ʂ��邽�߁j
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
          myColor=0;//player1:��
          myTurn=1;
          myIcon=blackIcon;
          yourIcon=whiteIcon;
        }else{
          myColor=1;//player2:��
          myTurn=0;
          myIcon=whiteIcon;
          yourIcon=blackIcon;
        }
        System.out.println(myTurn);
				while(true) {
					String inputLine = br.readLine();//�f�[�^����s�������ǂݍ���ł݂�
					if (inputLine != null) {//�ǂݍ��񂾂Ƃ��Ƀf�[�^���ǂݍ��܂ꂽ���ǂ������`�F�b�N����
						System.out.println(inputLine);//�f�o�b�O�i����m�F�p�j�ɃR���\�[���ɏo�͂���
						String[] inputTokens = inputLine.split(" ");	//���̓f�[�^����͂��邽�߂ɁA�X�y�[�X�Ő؂蕪����
						String cmd = inputTokens[0];//�R�}���h�̎��o���D�P�ڂ̗v�f�����o��
						/*if(cmd.equals("MOVE")){//cmd�̕�����"MOVE"�����������ׂ�D��������true�ƂȂ�
							//MOVE�̎��̏���(�R�}�̈ړ��̏���)
							String theBName = inputTokens[1];//�{�^���̖��O�i�ԍ��j�̎擾
							int theBnum = Integer.parseInt(theBName);//�{�^���̖��O�𐔒l�ɕϊ�����
							int x = Integer.parseInt(inputTokens[2]);//���l�ɕϊ�����
							int y = Integer.parseInt(inputTokens[3]);//���l�ɕϊ�����
							buttonArray[theBnum].setLocation(x,y);//�w��̃{�^�����ʒu��x,y�ɐݒ肷��
						}*/
              if(cmd.equals("PASS")){
                myTurn = 1 - myTurn;
              }
              if(cmd.equals("PLACE")){
                myTurn = 1 - myTurn;
                String theBName = inputTokens[1];//�{�^���̖��O�i�ԍ��j�̎擾
                int theBnum = Integer.parseInt(theBName);//�{�^���̖��O�𐔒l�ɕϊ�����
                int i = theBnum / 8;//(�^�e�A���R)=(2, 3)�̂Ƃ��A�^�e�~8+���R���A19�Ԗڂ̃{�^���B
                int j = theBnum % 8;// ���������āAi�̓��R�Aj�̓^�e��\��
                int theColor = Integer.parseInt(inputTokens[2]);//���l�ɕϊ�����
                if(theColor==myColor){
                  buttonArray[i][j].setIcon(myIcon);//blackIcon�ɐݒ肷��
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
                  System.out.println("���̐�"+whiteIconCount);
                  System.out.println("���̐�"+blackIconCount);
                  if(whiteIconCount==0 && myIcon == whiteIcon || blackIconCount==0 && myIcon == blackIcon){
                     System.out.println("����");
                  }else if(whiteIconCount>0 && myIcon == whiteIcon || blackIconCount>0 && myIcon == blackIcon){
                     System.out.println("����");
                  }
                }
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
  	
	public void mouseClicked(MouseEvent e) {//�{�^�����N���b�N�����Ƃ��̏���
    
    if(myTurn==1){
      System.out.println("�N���b�N");
      JButton theButton = (JButton)e.getComponent();//�N���b�N�����I�u�W�F�N�g�𓾂�D�^���Ⴄ�̂ŃL���X�g����
      String theArrayIndex = theButton.getActionCommand();//�{�^���̔z��̔ԍ������o��
      Icon theIcon = theButton.getIcon();//theIcon�ɂ́C���݂̃{�^���ɐݒ肳�ꂽ�A�C�R��������

      if(theArrayIndex.equals("PASS")){
        String msg = "PASS";
        out.println(msg);//���M�f�[�^���t���b�V���i�l�b�g���[�N��ɂ͂��o���j����
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
        //�u����
        String msg = "PLACE"+" "+theArrayIndex+" "+myColor;
        out.println(msg);//���M�f�[�^���t���b�V���i�l�b�g���[�N��ɂ͂��o���j����
        out.flush();
      } else {
        //�u���Ȃ�
        System.out.println("�����ɂ͔z�u�ł��܂���");
      }
      }
      repaint();//��ʂ̃I�u�W�F�N�g��`�悵����
    }
	}
	
	public void mouseEntered(MouseEvent e) {//�}�E�X���I�u�W�F�N�g�ɓ������Ƃ��̏���
		/*System.out.println("�}�E�X��������");*/
	}
	
	public void mouseExited(MouseEvent e) {//�}�E�X���I�u�W�F�N�g����o���Ƃ��̏���
		/*System.out.println("�}�E�X�E�o");*/
	}
	
	public void mousePressed(MouseEvent e) {//�}�E�X�ŃI�u�W�F�N�g���������Ƃ��̏����i�N���b�N�Ƃ̈Ⴂ�ɒ��Ӂj
		/*System.out.println("�}�E�X��������");*/
	}
	
	public void mouseReleased(MouseEvent e) {//�}�E�X�ŉ����Ă����I�u�W�F�N�g�𗣂����Ƃ��̏���
		/*System.out.println("�}�E�X�������");*/
	}
	
	public void mouseDragged(MouseEvent e) {//�}�E�X�ŃI�u�W�F�N�g�Ƃ��h���b�O���Ă���Ƃ��̏���
		/* System.out.println("�}�E�X���h���b�O");
		JButton theButton = (JButton)e.getComponent();//�^���Ⴄ�̂ŃL���X�g����
		String theArrayIndex = theButton.getActionCommand();//�{�^���̔z��̔ԍ������o��
    if(!theArrayIndex.equals("0")){
      Point theMLoc = e.getPoint();//�������R���|�[�l���g����Ƃ��鑊�΍��W
      System.out.println(theMLoc);//�f�o�b�O�i�m�F�p�j�ɁC�擾�����}�E�X�̈ʒu���R���\�[���ɏo�͂���
      Point theBtnLocation = theButton.getLocation();//�N���b�N�����{�^�������W���擾����
      theBtnLocation.x += theMLoc.x-15;//�{�^���̐^�񒆓�����Ƀ}�E�X�J�[�\��������悤�ɕ␳����
      theBtnLocation.y += theMLoc.y-15;//�{�^���̐^�񒆓�����Ƀ}�E�X�J�[�\��������悤�ɕ␳����
  
      //���M�����쐬����i��M���ɂ́C���̑��������ԂɃf�[�^�����o���D�X�y�[�X���f�[�^�̋�؂�ƂȂ�j
      String msg = "MOVE"+" "+theArrayIndex+" "+theBtnLocation.x+" "+theBtnLocation.y;

      //�T�[�o�ɏ��𑗂�
      out.println(msg);//���M�f�[�^���o�b�t�@�ɏ����o��
      out.flush();//���M�f�[�^���t���b�V���i�l�b�g���[�N��ɂ͂��o���j����

      repaint();//�I�u�W�F�N�g�̍ĕ`����s��
    }*/
	}

	public void mouseMoved(MouseEvent e) {//�}�E�X���I�u�W�F�N�g��ňړ������Ƃ��̏���
		/*System.out.println("�}�E�X�ړ�");
		int theMLocX = e.getX();//�}�E�X��x���W�𓾂�
		int theMLocY = e.getY();//�}�E�X��y���W�𓾂�
		System.out.println(theMLocX+","+theMLocY);//�R���\�[���ɏo�͂���
    */
	}

  public void actionPerformed(ActionEvent e) {
        System.out.println("�A�N�V��������");
        System.out.println(e.getSource());
        String theCmd = e.getActionCommand();
        System.out.println("ActionCommand: "+theCmd);
        Object theObj = e.getSource();
        System.out.println("�N���X����" + theObj.getClass().getName());
        String theClass = theObj.getClass().getName();//�N���X�����g���ē�����ς���

        theSoundPlayer2 = new SoundPlayer("443_2.wav");
        theSoundPlayer2.play();

  }

  // �u����Ֆʂ��ǂ����𔻒肷��֐�
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

  // ���W��8�~8�̃}�X�ڂ𒴂��Ă��邩�ǂ����𔻒肷��֐�
  public boolean isExceededArea(int posY, int posX){
    return posX < 0 || posY < 0 || posX > 7 || posY > 7;
  }

  public int generatePos(int theArrayIndex){
    int posX = theArrayIndex % 8;
    int posY = theArrayIndex / 8;
    return posX;
  }

  // �u����Ֆʂ��ǂ����𔻒肷��֐�
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
                System.out.println("PlaySound time="+time);
                long endTime = System.currentTimeMillis()+time*1000/44100;
                clip.start();
                System.out.println("PlaySound time="+(int)(time/44100));
                while(true){
                    if(stopFlag){//stopFlag��true�ɂȂ����I��
                        System.out.println("PlaySound stop by stopFlag");
                        clip.stop();
                        return;
                    }
                    System.out.println("endTime="+endTime);
                    System.out.println("currentTimeMillis="+System.currentTimeMillis());
                    if(endTime < System.currentTimeMillis()){//�Ȃ̒������߂�����I��
                        System.out.println("PlaySound stop by sound length");
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
        System.out.println("StopSound");
    }

}


}
