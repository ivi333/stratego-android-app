package de.arvato.stratego;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import de.arvato.stratego.game.PieceEnum;
import de.arvato.stratego.util.SpacingItemDecoration;

public class StrategoView implements Observer {

    public static final String TAG = "StrategoView";

    private StrategoViewBase strategoViewBase;
    private StrategoActivity parent;
    //private LayoutInflater inflater;
    private StrategoControl strategoControl;
    private ViewAnimator viewAnimator;
    private int selectedPos;
    private List<Integer> nextMovements;
    private int player;
    private Timer timer;
    private TextView textViewClockTimeTop, textViewClockTimeBottom;
    //private ImageView pieceFight1, pieceFight2;
    //private View fightView;
    private Drawable drawableFightWin, drawableFightLost;
    private Pair latestFight;
    private TextView winnerTextView;

    private RecyclerView recyclerViewRed;
    private RecyclerView recyclerViewBlue;

    private StrategoCapturedPieceAdapter adapterBlue;
    private StrategoCapturedPieceAdapter adapterRed;

    private List<CapturedPieceItem> listCapturedPiecesBlue;
    private List<CapturedPieceItem> listCapturedPiecesRed;

    @Override
    public void update(Observable o, Object arg) {
        updateStatus ();
        paintBoard();
    }

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
        strategoControl.addObserver(this);

        parent = (StrategoActivity) activity;
        strategoViewBase = new StrategoViewBase(activity, player);
        //inflater = (LayoutInflater) parent.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

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
        Log.i(TAG, "handleClick at index:" + index);
        nextMovements = Collections.emptyList();
        if (strategoControl.selectPiece(index)) {
            Log.d (TAG, "Piece at position:" + index + " has been selected.");
            nextMovements = strategoControl.getBoard().getPossibleMovements (index);
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
                checkFinishedGame ();
                selectedPos = -1;
            }
        }
        strategoViewBase.paintBoard(strategoControl, selectedPos, nextMovements);
        updateStatus();
    }

    private void checkFinishedGame() {
        if (strategoControl.getBoard().getGameStatus() == StrategoConstants.GameStatus.FINISH) {
            viewAnimator.setDisplayedChild(0);
            int winner = strategoControl.getWinner ();
            if (StrategoConstants.BLUE == winner) {
                winnerTextView.setText("Blue Flag Captured!");
                winnerTextView.setTextColor(Color.BLUE);
            } else {
                winnerTextView.setText("Red Flag Captured!");
                winnerTextView.setTextColor(Color.RED);
            }
            strategoControl.stopTimer();
            winnerTextView.setVisibility(View.VISIBLE);
            Animation bounceAnimation = AnimationUtils.loadAnimation(parent.getApplicationContext(), R.anim.bounce);
            winnerTextView.startAnimation(bounceAnimation);
        }
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
        Button bPlayerReset = parent.findViewById(R.id.PlayerReset);
        Button bPlayerRandom = parent.findViewById(R.id.PlayerRandom);
        TextView InfoText = parent.findViewById(R.id.InfoText);
        TextView InfoText2 = parent.findViewById(R.id.InfoText2);
        TextView InfoText3 = parent.findViewById(R.id.InfoText3);
        winnerTextView = parent.findViewById(R.id.winnerTextView);

        bPlayerReady.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean b = strategoControl.startGame();
                if (!b) {
                    Toast.makeText(parent.getApplicationContext(), "Fill all pieces!", Toast.LENGTH_SHORT).show();
                } else {
                    viewAnimator.setDisplayedChild(1);
                    InfoText.setVisibility(View.GONE);
                    InfoText2.setVisibility(View.GONE);
                    InfoText3.setVisibility(View.GONE);
                    bPlayerReady.setEnabled(false);
                    bPlayerRandom.setEnabled(false);
                }
            }
        }
        );

        bPlayerReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                winnerTextView.setVisibility(View.GONE);

                // Init the captured View
                //initCapturedImages ();

                // Timer
                //initTimer ();

                strategoControl.resetGame();

                // Paint
                paintBoard();

            }
        });

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
        //Recycler view for Red
        recyclerViewRed = parent.findViewById(R.id.recyclerViewRed);
        GridLayoutManager gridLayoutManagerRed = new GridLayoutManager(parent, 2);
        recyclerViewRed.setLayoutManager(gridLayoutManagerRed);
        //set spacing between columns
        int spacingInPixels = parent.getResources().getDimensionPixelSize(R.dimen.grid_item_spacing);
        recyclerViewRed.addItemDecoration(new SpacingItemDecoration(spacingInPixels));

        /*LinearLayoutManager layout = new LinearLayoutManager(parent);
        layout.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layout);*/

        listCapturedPiecesRed = new ArrayList<>();
        listCapturedPiecesRed.add(new CapturedPieceItem(PieceEnum.MARSHALL, StrategoConstants.RED, 0));
        listCapturedPiecesRed.add(new CapturedPieceItem(PieceEnum.GENERAL, StrategoConstants.RED, 0));
        listCapturedPiecesRed.add(new CapturedPieceItem(PieceEnum.COLONEL, StrategoConstants.RED, 0));
        listCapturedPiecesRed.add(new CapturedPieceItem(PieceEnum.MAJOR, StrategoConstants.RED, 0));
        listCapturedPiecesRed.add(new CapturedPieceItem(PieceEnum.CAPTAIN, StrategoConstants.RED, 0));
        listCapturedPiecesRed.add(new CapturedPieceItem(PieceEnum.LIEUTENANT, StrategoConstants.RED, 0));
        listCapturedPiecesRed.add(new CapturedPieceItem(PieceEnum.SERGEANT, StrategoConstants.RED, 0));
        listCapturedPiecesRed.add(new CapturedPieceItem(PieceEnum.MINER, StrategoConstants.RED, 0));
        listCapturedPiecesRed.add(new CapturedPieceItem(PieceEnum.SCOUT, StrategoConstants.RED, 0));
        listCapturedPiecesRed.add(new CapturedPieceItem(PieceEnum.SPY, StrategoConstants.RED, 0));
        listCapturedPiecesRed.add(new CapturedPieceItem(PieceEnum.BOMB, StrategoConstants.RED, 0));
        listCapturedPiecesRed.add(new CapturedPieceItem(PieceEnum.FLAG, StrategoConstants.RED, 0));
        adapterRed = new StrategoCapturedPieceAdapter(listCapturedPiecesRed);
        recyclerViewRed.setAdapter(adapterRed);

        //Recycler view for Blue
        recyclerViewBlue = parent.findViewById(R.id.recyclerViewBlue);
        GridLayoutManager gridLayoutManagerBlue = new GridLayoutManager(parent, 2);
        recyclerViewBlue.setLayoutManager(gridLayoutManagerBlue);

        recyclerViewBlue.addItemDecoration(new SpacingItemDecoration(spacingInPixels));
        listCapturedPiecesBlue = new ArrayList<>();
        listCapturedPiecesBlue.add(new CapturedPieceItem(PieceEnum.MARSHALL, StrategoConstants.BLUE, 0));
        listCapturedPiecesBlue.add(new CapturedPieceItem(PieceEnum.GENERAL, StrategoConstants.BLUE, 0));
        listCapturedPiecesBlue.add(new CapturedPieceItem(PieceEnum.COLONEL, StrategoConstants.BLUE, 0));
        listCapturedPiecesBlue.add(new CapturedPieceItem(PieceEnum.MAJOR, StrategoConstants.BLUE, 0));
        listCapturedPiecesBlue.add(new CapturedPieceItem(PieceEnum.CAPTAIN, StrategoConstants.BLUE, 0));
        listCapturedPiecesBlue.add(new CapturedPieceItem(PieceEnum.LIEUTENANT, StrategoConstants.BLUE, 0));
        listCapturedPiecesBlue.add(new CapturedPieceItem(PieceEnum.SERGEANT, StrategoConstants.BLUE, 0));
        listCapturedPiecesBlue.add(new CapturedPieceItem(PieceEnum.MINER, StrategoConstants.BLUE, 0));
        listCapturedPiecesBlue.add(new CapturedPieceItem(PieceEnum.SCOUT, StrategoConstants.BLUE, 0));
        listCapturedPiecesBlue.add(new CapturedPieceItem(PieceEnum.SPY, StrategoConstants.BLUE, 0));
        listCapturedPiecesBlue.add(new CapturedPieceItem(PieceEnum.BOMB, StrategoConstants.BLUE, 0));
        listCapturedPiecesBlue.add(new CapturedPieceItem(PieceEnum.FLAG, StrategoConstants.BLUE, 0));
        adapterBlue = new StrategoCapturedPieceAdapter(listCapturedPiecesBlue);
        recyclerViewBlue.setAdapter(adapterBlue);
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
        //TODO
        //pieceFight1 = parent.findViewById(R.id.pieceFightRed);
        //pieceFight2 =  parent.findViewById(R.id.pieceFightBlue);
        //fightView = parent.findViewById(R.id.p1vsp2);
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
        updateCapturedPieces (StrategoConstants.RED, strategoControl.getBoard().getCapturedPiecesRed());
        updateCapturedPieces (StrategoConstants.BLUE, strategoControl.getBoard().getCapturedPiecesBlue());
    }

    private String formatTime(long msec) {
        return  String.format("%02d:%02d", (int) (Math.floor(msec / 60000)), ((int) (msec / 1000) % 60));
    }

    public StrategoControl getStrategoControl() {
        return strategoControl;
    }

    private void updateFight () {
        //TODO
//        List<Pair> fights = strategoControl.getFights();
//        if (!fights.isEmpty()) {
//            Pair fight = fights.get(fights.size() - 1);
//            if (fight.equals(latestFight)) {
//                return;
//            }
//            latestFight = fight;
//            Piece piece1 = (Piece) fight.first;
//            Piece piece2 = (Piece) fight.second;
//
//            PieceFight.PieceFighStatus pieceFightStatus = PieceFight.fight(piece1, piece2);
//            pieceFight1.setImageBitmap(StrategoImageView.arrPieceBitmaps[piece1.getPlayer()][piece1.getPieceEnum().getId()]);
//            pieceFight2.setImageBitmap(StrategoImageView.arrPieceBitmaps[piece2.getPlayer()][piece2.getPieceEnum().getId()]);
//
//            FrameLayout parentFight1 = (FrameLayout) pieceFight1.getParent();
//            FrameLayout parentFight2 = (FrameLayout) pieceFight2.getParent();
//
//            //NO_FIGHT, PIECE1_WIN, PIECE2_WIN, TIE, FLAG_CAPTURED
//            if (pieceFightStatus == PieceFight.PieceFighStatus.PIECE1_WIN ||
//                    pieceFightStatus == PieceFight.PieceFighStatus.FLAG_CAPTURED) {
//                parentFight1.setForeground(drawableFightWin);
//                parentFight2.setForeground(drawableFightLost);
//            } else if (pieceFightStatus == PieceFight.PieceFighStatus.PIECE2_WIN) {
//                parentFight2.setForeground(drawableFightWin);
//                parentFight1.setForeground(drawableFightLost);
//            } else if (pieceFightStatus == PieceFight.PieceFighStatus.TIE) {
//                parentFight2.setForeground(drawableFightLost);
//                parentFight1.setForeground(drawableFightLost);
//            } else {
//                Log.e(TAG, "Pieces are not fighting!");
//            }
//        }
    }

    private void updateCapturedPieces(int player, Map<PieceEnum, Integer> mapCapturedPieces) {
        if (mapCapturedPieces == null) {
            return;
        }

        List<CapturedPieceItem> temporary = StrategoConstants.RED == player ? listCapturedPiecesRed : listCapturedPiecesBlue;
        StrategoCapturedPieceAdapter temporaryAdapter = StrategoConstants.RED == player ? adapterRed : adapterBlue;

        boolean changes=false;
        for (CapturedPieceItem capturePiece : temporary) {
            if (mapCapturedPieces.containsKey(capturePiece.getPieceEnum())) {
                Integer total = mapCapturedPieces.get(capturePiece.getPieceEnum());
                if (total!=null && total >0 && capturePiece.getCaptured() != total) {
                    capturePiece.setCaptured(total);
                    if (capturePiece.getPieceEnum().maxLives() == total) {
                        capturePiece.setDead(true);
                    }
                    changes=true;
                }
            }
        }
        if (changes) {
            temporaryAdapter.notifyDataSetChanged();
        }
    }
}
