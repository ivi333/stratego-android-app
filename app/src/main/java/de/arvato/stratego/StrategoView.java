package de.arvato.stratego;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import androidx.core.content.ContextCompat;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class StrategoView {

    public static final String TAG = "StrategoView";

    private StrategoViewBase strategoViewBase;
    private StrategoActivity parent;
    private LayoutInflater inflater;
    private StrategoControl strategoControl;
    private ViewAnimator viewAnimator;
    private StrategoCapturedImageView[][] arrImageCaptured;
    private TextView[][] arrTextCaptured;
    private int selectedPos;
    private List<Integer> nextMovements;
    private int player;
    private Timer timer;
    private TextView textViewClockTimeTop, textViewClockTimeBottom;
    private ImageView pieceFight1, pieceFight2;
    private View fightView;
    private Drawable drawableFightWin, drawableFightLost;
    private Pair latestFight;

    static class StrategoInnerHandler extends Handler {
        WeakReference<StrategoView> _strategoView;

        StrategoInnerHandler(StrategoView view) {
            _strategoView = new WeakReference<StrategoView>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            StrategoView strategoView = _strategoView.get();
            if (strategoView != null) {
                long lTmp;
                lTmp = strategoView.getStrategoControl().getRedRemainClock();
                if (lTmp < 0) {
                    lTmp = -lTmp;
                }
                strategoView.textViewClockTimeBottom.setText(strategoView.formatTime(lTmp));

                lTmp = strategoView.getStrategoControl().getBlueRemainClock();
                if (lTmp < 0) {
                    lTmp = -lTmp;
                }
                strategoView.textViewClockTimeTop.setText(strategoView.formatTime(lTmp));
            }
        }
    }

    protected StrategoInnerHandler m_timerHandler;


    public StrategoView(Activity activity) {
        super();
        selectedPos=-1;
        //select your player
        player = StrategoConstants.RED;
        strategoControl = new StrategoControl(player);
        parent = (StrategoActivity) activity;
        strategoViewBase = new StrategoViewBase(activity, player);
        inflater = (LayoutInflater) parent.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        drawableFightWin = ContextCompat.getDrawable(parent.getApplicationContext(), R.drawable.fight_win);
        drawableFightLost =  ContextCompat.getDrawable(parent.getApplicationContext(), R.drawable.fight_lost);

        //Listeners
        View.OnClickListener ocl = new View.OnClickListener() {
            public void onClick(View arg0) {
                handleClick(strategoViewBase.getIndexOfButton(arg0));
            }
        };
        View.OnLongClickListener olcl = new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
                handleClick(strategoViewBase.getIndexOfButton(view));
                return true;
            }
        };
        strategoViewBase.init(ocl, olcl);

        //View Animator
        viewAnimator = (ViewAnimator) parent.findViewById(R.id.ViewAnimatorMain);
        if (viewAnimator != null) {
            viewAnimator.setOutAnimation(parent, R.anim.slide_left);
            viewAnimator.setInAnimation(parent, R.anim.slide_right);
        }

        //View bottomPlayLayoutView = parent.findViewById(R.id.bottomPlayLayout);

        // Init the board with random pieces
        if (!strategoControl.fakeGame) {
            strategoControl.randomPieces(player);
            if (player == StrategoConstants.RED) {
                strategoControl.randomPieces(StrategoConstants.BLUE);
            } else {
                strategoControl.randomPieces(StrategoConstants.RED);
            }
        }

        // Init the captured View
        initCapturedImages ();

        initDistributePieces () ;

        // Timer
        initTimer ();

        initFightView();

        // Paint
        paintBoard();

    }

    public void handleClick(int index) {
        Log.d(TAG, "handleClick at index:" + index);
        nextMovements = Collections.emptyList();
        if (strategoControl.selectPiece(index)) {
            Log.d (TAG, "Piece at position:" + index + " has been selected.");
            nextMovements = strategoControl.getPossibleMovements (index);
            selectedPos = index;
            StringBuilder sb = new StringBuilder();
            for (Integer i : nextMovements) {
                sb.append(i);
                sb.append(",");
            }
            if (sb.length() > 0) sb.deleteCharAt(sb.length()-1);
            Log.d(TAG, "Possible movements at index:" + index + " = " + sb.toString());
        } else {
            if (selectedPos != -1) {
                Log.d (TAG, "Move Piece to target index:" + index);
                strategoControl.movePiece(index);
                selectedPos = -1;
            }
        }
        strategoViewBase.paintBoard(strategoControl, selectedPos, nextMovements);
        updateStatus();
    }

    private void initDistributePieces() {
        //TODO Add a grid with initial pieces? or just initialize it random
        /*GridView gridView = parent.findViewById(R.id.startGameGrid);
        PieceEnum startPieces [] = StrategoControl.createStartPiece(this.player);
        StrategoPieceAdapter strategoPieceAdapter = new StrategoPieceAdapter(parent.getApplicationContext(), startPieces, player);
        gridView.setAdapter(strategoPieceAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d(TAG, "Item clicked: " + position + " id:" + id);
                    startPieces[position] = null;
                    strategoPieceAdapter.notifyDataSetChanged();
                }
            }
        );*/

        Button bPlayerReady = parent.findViewById(R.id.PlayerReady);
        Button bPlayerRandom = parent.findViewById(R.id.PlayerRandom);

        bPlayerReady.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean b = strategoControl.startGame();
                if (!b) {
                    Toast.makeText(parent.getApplicationContext(), "Fill all pieces!", Toast.LENGTH_SHORT).show();
                } else {
                    //TODO Game can start waiting for oponent in multiplayer?
                }
            }
        }
        );

        bPlayerRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strategoControl.randomPieces(player);
                paintBoard ();
            }
        }
        );
    }

    private void initCapturedImages() {
        arrImageCaptured = new StrategoCapturedImageView[2][12];
        arrImageCaptured[StrategoConstants.RED][PieceEnum.MARSHALL.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedRedMarshall);
        arrImageCaptured[StrategoConstants.RED][PieceEnum.MARSHALL.getId()].initBitmap("mariscal_red.png");
        arrImageCaptured[StrategoConstants.RED][PieceEnum.GENERAL.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedRedGeneral);
        arrImageCaptured[StrategoConstants.RED][PieceEnum.GENERAL.getId()].initBitmap("general_red.png");
        arrImageCaptured[StrategoConstants.RED][PieceEnum.COLONEL.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedRedColonel);
        arrImageCaptured[StrategoConstants.RED][PieceEnum.COLONEL.getId()].initBitmap("coronel_red.png");
        arrImageCaptured[StrategoConstants.RED][PieceEnum.LIEUTENANT.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedRedLieutenant);
        arrImageCaptured[StrategoConstants.RED][PieceEnum.LIEUTENANT.getId()].initBitmap("lituant_red.png");
        arrImageCaptured[StrategoConstants.RED][PieceEnum.CAPTAIN.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedRedCaptain);
        arrImageCaptured[StrategoConstants.RED][PieceEnum.CAPTAIN.getId()].initBitmap("capitan_red.png");
        arrImageCaptured[StrategoConstants.RED][PieceEnum.MAJOR.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedRedMajor);
        arrImageCaptured[StrategoConstants.RED][PieceEnum.MAJOR.getId()].initBitmap("major_red.png");
        arrImageCaptured[StrategoConstants.RED][PieceEnum.SERGEANT.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedRedSergeant);
        arrImageCaptured[StrategoConstants.RED][PieceEnum.SERGEANT.getId()].initBitmap("sargento_red.png");
        arrImageCaptured[StrategoConstants.RED][PieceEnum.MINER.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedRedMiner);
        arrImageCaptured[StrategoConstants.RED][PieceEnum.MINER.getId()].initBitmap("minero_red.png");
        arrImageCaptured[StrategoConstants.RED][PieceEnum.SCOUT.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedRedScout);
        arrImageCaptured[StrategoConstants.RED][PieceEnum.SCOUT.getId()].initBitmap("scout_red.png");
        arrImageCaptured[StrategoConstants.RED][PieceEnum.SPY.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedRedSpy);
        arrImageCaptured[StrategoConstants.RED][PieceEnum.SPY.getId()].initBitmap("spy_red.png");
        arrImageCaptured[StrategoConstants.RED][PieceEnum.BOMB.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedRedBomb);
        arrImageCaptured[StrategoConstants.RED][PieceEnum.BOMB.getId()].initBitmap("bomb_red.png");
        arrImageCaptured[StrategoConstants.RED][PieceEnum.FLAG.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedRedFlag);
        arrImageCaptured[StrategoConstants.RED][PieceEnum.FLAG.getId()].initBitmap("bandera_red.png");
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.MARSHALL.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedBlueMarshall);
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.MARSHALL.getId()].initBitmap("mariscal_blue.png");
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.GENERAL.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedBlueGeneral);
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.GENERAL.getId()].initBitmap("general_blue.png");
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.COLONEL.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedBlueColonel);
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.COLONEL.getId()].initBitmap("coronel_blue.png");
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.LIEUTENANT.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedBlueLieutenant);
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.LIEUTENANT.getId()].initBitmap("lituant_blue.png");
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.CAPTAIN.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedBlueCaptain);
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.CAPTAIN.getId()].initBitmap("capitan_blue.png");
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.MAJOR.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedBlueMajor);
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.MAJOR.getId()].initBitmap("major_blue.png");
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.SERGEANT.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedBlueSergeant);
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.SERGEANT.getId()].initBitmap("sargento_blue.png");
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.MINER.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedBlueMiner);
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.MINER.getId()].initBitmap("minero_blue.png");
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.SCOUT.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedBlueScout);
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.SCOUT.getId()].initBitmap("scout_blue.png");
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.SPY.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedBlueSpy);
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.SPY.getId()].initBitmap("spy_blue.png");
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.BOMB.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedBlueBomb);
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.BOMB.getId()].initBitmap("bomb_blue.png");
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.FLAG.getId()] = (StrategoCapturedImageView) parent.findViewById(R.id.ImageCapturedBlueFlag);
        arrImageCaptured[StrategoConstants.BLUE][PieceEnum.FLAG.getId()].initBitmap("bandera_blue.png");

        arrTextCaptured = new TextView[2][12];
        arrTextCaptured[StrategoConstants.RED][PieceEnum.MARSHALL.getId()] =  parent.findViewById(R.id.TextViewCapturedRedMarshall);
        arrTextCaptured[StrategoConstants.RED][PieceEnum.GENERAL.getId()] =  parent.findViewById(R.id.TextViewCapturedRedGeneral);
        arrTextCaptured[StrategoConstants.RED][PieceEnum.COLONEL.getId()] =  parent.findViewById(R.id.TextViewCapturedRedColonel);
        arrTextCaptured[StrategoConstants.RED][PieceEnum.LIEUTENANT.getId()] =  parent.findViewById(R.id.TextViewCapturedRedLieutenant);
        arrTextCaptured[StrategoConstants.RED][PieceEnum.CAPTAIN.getId()] =  parent.findViewById(R.id.TextViewCapturedRedCaptain);
        arrTextCaptured[StrategoConstants.RED][PieceEnum.MAJOR.getId()] =  parent.findViewById(R.id.TextViewCapturedRedMajor);
        arrTextCaptured[StrategoConstants.RED][PieceEnum.SERGEANT.getId()] =  parent.findViewById(R.id.TextViewCapturedRedSergeant);
        arrTextCaptured[StrategoConstants.RED][PieceEnum.MINER.getId()] =  parent.findViewById(R.id.TextViewCapturedRedMiner);
        arrTextCaptured[StrategoConstants.RED][PieceEnum.SCOUT.getId()] =  parent.findViewById(R.id.TextViewCapturedRedScout);
        arrTextCaptured[StrategoConstants.RED][PieceEnum.SPY.getId()] =  parent.findViewById(R.id.TextViewCapturedRedSpy);
        arrTextCaptured[StrategoConstants.RED][PieceEnum.BOMB.getId()] =  parent.findViewById(R.id.TextViewCapturedRedBomb);
        arrTextCaptured[StrategoConstants.RED][PieceEnum.FLAG.getId()] =  parent.findViewById(R.id.TextViewCapturedRedFlag);
        arrTextCaptured[StrategoConstants.BLUE][PieceEnum.MARSHALL.getId()] =  parent.findViewById(R.id.TextViewCapturedBlueMarshall);
        arrTextCaptured[StrategoConstants.BLUE][PieceEnum.GENERAL.getId()] =  parent.findViewById(R.id.TextViewCapturedBlueGeneral);
        arrTextCaptured[StrategoConstants.BLUE][PieceEnum.COLONEL.getId()] =  parent.findViewById(R.id.TextViewCapturedBlueColonel);
        arrTextCaptured[StrategoConstants.BLUE][PieceEnum.LIEUTENANT.getId()] =  parent.findViewById(R.id.TextViewCapturedBlueLieutenant);
        arrTextCaptured[StrategoConstants.BLUE][PieceEnum.CAPTAIN.getId()] =  parent.findViewById(R.id.TextViewCapturedBlueCaptain);
        arrTextCaptured[StrategoConstants.BLUE][PieceEnum.MAJOR.getId()] =  parent.findViewById(R.id.TextViewCapturedBlueMajor);
        arrTextCaptured[StrategoConstants.BLUE][PieceEnum.SERGEANT.getId()] =  parent.findViewById(R.id.TextViewCapturedBlueSergeant);
        arrTextCaptured[StrategoConstants.BLUE][PieceEnum.MINER.getId()] =  parent.findViewById(R.id.TextViewCapturedBlueMiner);
        arrTextCaptured[StrategoConstants.BLUE][PieceEnum.SCOUT.getId()] =  parent.findViewById(R.id.TextViewCapturedBlueScout);
        arrTextCaptured[StrategoConstants.BLUE][PieceEnum.SPY.getId()] =  parent.findViewById(R.id.TextViewCapturedBlueSpy);
        arrTextCaptured[StrategoConstants.BLUE][PieceEnum.BOMB.getId()] =  parent.findViewById(R.id.TextViewCapturedBlueBomb);
        arrTextCaptured[StrategoConstants.BLUE][PieceEnum.FLAG.getId()] =  parent.findViewById(R.id.TextViewCapturedBlueFlag);

        for (int i=0;i<2;i++) {
            for (int j=0;j<12;j++) {
                arrImageCaptured[i][j].setVisibility(View.INVISIBLE);
                arrTextCaptured[i][j].setVisibility(View.INVISIBLE);
            }
        }
    }

    public void paintBoard () {
        strategoViewBase.paintBoard(strategoControl, -1, Collections.emptyList());
    }

    public void initTimer () {
        textViewClockTimeTop = parent.findViewById(R.id.TextViewClockTimeTop);
        textViewClockTimeBottom = parent.findViewById(R.id.TextViewClockTimeBottom);
        m_timerHandler = new StrategoInnerHandler(this);
        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 1;
                m_timerHandler.sendMessage(msg);
            }
        }, 1000, 1000);
    }

    public void initFightView () {
        pieceFight1 = parent.findViewById(R.id.pieceFightRed);
        pieceFight2 =  parent.findViewById(R.id.pieceFightBlue);
        fightView = parent.findViewById(R.id.p1vsp2);
        //pieceFight1.setImageBitmap(StrategoImageView.arrPieceBitmaps[StrategoConstants.RED][PieceEnum.MINER.getId()]);
        //pieceFight2.setImageBitmap(StrategoImageView.arrPieceBitmaps[StrategoConstants.BLUE][PieceEnum.SCOUT.getId()]);
    }

    public void showNext() {
        viewAnimator.showNext();
    }

    public void showPrevious() {
        viewAnimator.showPrevious();
    }

    public void updateStatus () {
        updateFight ();
        updateCapturedPieces (StrategoConstants.RED, strategoControl.getCapturedPiecesRed());
        updateCapturedPieces (StrategoConstants.BLUE, strategoControl.getCapturedPiecesBlue());
    }

    private String formatTime(long msec) {
        final String sTmp = String.format("%02d:%02d", (int) (Math.floor(msec / 60000)), ((int) (msec / 1000) % 60));
        return sTmp;
    }

    public StrategoControl getStrategoControl() {
        return strategoControl;
    }

    private void updateFight () {
        List<Pair> fights = strategoControl.getFights();
        if (!fights.isEmpty()) {
            Pair fight = fights.get(fights.size() - 1);
            if (fight.equals(latestFight)) {
                return;
            }
            latestFight = fight;
            Piece piece1 = (Piece) fight.first;
            Piece piece2 = (Piece) fight.second;

            PieceFight.PieceFlighStatus pieceFightStatus = PieceFight.fight(piece1, piece2);
            pieceFight1.setImageBitmap(StrategoImageView.arrPieceBitmaps[piece1.getPlayer()][piece1.getPieceEnum().getId()]);
            pieceFight2.setImageBitmap(StrategoImageView.arrPieceBitmaps[piece2.getPlayer()][piece2.getPieceEnum().getId()]);

            FrameLayout parentFight1 = (FrameLayout) pieceFight1.getParent();
            FrameLayout parentFight2 = (FrameLayout) pieceFight2.getParent();

            //NO_FIGHT, PIECE1_WIN, PIECE2_WIN, TIE, FLAG_CAPTURED
            if (pieceFightStatus == PieceFight.PieceFlighStatus.PIECE1_WIN ||
                    pieceFightStatus == PieceFight.PieceFlighStatus.FLAG_CAPTURED) {
                parentFight1.setForeground(drawableFightWin);
                parentFight2.setForeground(drawableFightLost);
            } else if (pieceFightStatus == PieceFight.PieceFlighStatus.PIECE2_WIN) {
                parentFight2.setForeground(drawableFightWin);
                parentFight1.setForeground(drawableFightLost);
            } else if (pieceFightStatus == PieceFight.PieceFlighStatus.TIE) {
                parentFight2.setForeground(drawableFightLost);
                parentFight1.setForeground(drawableFightLost);
            } else {
                Log.e(TAG, "Pieces are not fighting!");
            }
        }
    }

    private void updateCapturedPieces(int player, Map<PieceEnum, Integer> capturedPieces) {
        for (Map.Entry<PieceEnum, Integer> entry : capturedPieces.entrySet()) {
            arrImageCaptured[player][entry.getKey().getId()].setVisibility(View.VISIBLE);
            arrTextCaptured[player][entry.getKey().getId()].setVisibility(View.VISIBLE);
            arrTextCaptured[player][entry.getKey().getId()].setText(String.valueOf(entry.getValue()));
        }
    }
}
