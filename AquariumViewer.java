/**
 * AquariumViewer represents an interface for playing a game of Aquarium.
 *
 * @author Lyndon While
 * @version 2020
 * @author Zach Manson (22903345), Oliver Lynch (22989775)
 * @version 20200512
 */
import java.awt.*;
import java.awt.event.*;
import java.time.*;

public class AquariumViewer implements MouseListener {
  private final int BOXSIZE = 40; // the size of each square
  private final int OFFSET = BOXSIZE * 2; // the gap around the board
  private int WINDOWSIZE; // set this in the constructor

  private Aquarium puzzle; // the internal representation of the puzzle
  private int size; // the puzzle is size x size
  private SimpleCanvas sc; // the display window

  private Color foreColor = Color.black;
  private Color backColor = Color.white;
  private Color waterColor = Color.blue;
  private Color altColor = Color.red;
  private boolean darkMode = false;

  private Clock clock = Clock.systemUTC();
  private Instant startTime;
  private Duration solveTime;

  private int solvePuzzleSize = 16;

  /**
   * Main constructor for objects of class AquariumViewer. Sets all fields, and displays the initial
   * puzzle.
   */
  public AquariumViewer(Aquarium puzzle) {
    // TODO 8 - complete
    this.puzzle = puzzle;
    size = puzzle.getSize();

    WINDOWSIZE = (OFFSET * 2) + (BOXSIZE * size);
    sc = new SimpleCanvas("Aquarium", WINDOWSIZE, WINDOWSIZE, backColor);

    sc.addMouseListener(this);

    this.displayPuzzle();

    startTime = clock.instant();
  }

  /** Selects from among the provided files in folder Examples. xyz selects axy_z.txt. */
  public AquariumViewer(int n) {
    this(CheckSolution.newPuzzle(n));
  }

  /** Uses the provided example file on the LMS page. */
  public AquariumViewer() {
    this(CheckSolution.newPuzzle(8));
  }

  /** Returns the current state of the puzzle. */
  public Aquarium getPuzzle() {
    // TODO 7a - complete
    return puzzle;
  }

  /** Returns the size of the puzzle. */
  public int getSize() {
    // TODO 7b - complete
    return size;
  }

  /** Returns the current state of the canvas. */
  public SimpleCanvas getCanvas() {
    // TODO 7c - complete
    return sc;
  }

  /** Displays the initial puzzle; see the LMS page for the format. */
  private void displayPuzzle() {
    // TODO 13 - complete
    Space[][] spaces = puzzle.getSpaces();
    System.out.println("spaces.length:" + spaces.length);
    for (int r = 0; r < spaces.length; r++) {
      for (int c = 0; c < spaces[r].length; c++) {
        this.updateSquare(r, c);
      }
    }

    this.displayGrid();
    this.displayButtons();
    this.displayNumbers();
    this.displayAquariums();
  }

  /** Displays the grid in the middle of the window. */
  public void displayGrid() {
    // TODO 9 - complete
    for (int s = 0; s < size + 1; s++) {
      sc.drawLine(
          OFFSET + (BOXSIZE * s),
          OFFSET,
          OFFSET + (BOXSIZE * s),
          OFFSET + (size * BOXSIZE),
          foreColor);

      sc.drawLine(
          OFFSET,
          OFFSET + (BOXSIZE * s),
          OFFSET + (size * BOXSIZE),
          OFFSET + (BOXSIZE * s),
          foreColor);
    }
  }

  /** Displays the numbers around the grid. */
  public void displayNumbers() {
    // TODO 10 - complete
    sc.setFont(new Font("Consolas", 1, 20));
    int[] rowTotals = puzzle.getRowTotals();
    for (int r = 0; r < rowTotals.length; r++) {
      sc.drawString(
          String.valueOf(rowTotals[r]),
          OFFSET - (BOXSIZE / 2) - 4,
          OFFSET + (BOXSIZE * r) + (BOXSIZE / 2) + 8,
          foreColor);
    }

    int[] columnTotals = puzzle.getColumnTotals();
    for (int c = 0; c < columnTotals.length; c++) {
      sc.drawString(
          String.valueOf(columnTotals[c]),
          OFFSET + (BOXSIZE * c) + (BOXSIZE / 2) - 4,
          OFFSET - (BOXSIZE / 2) + 8,
          foreColor);
    }
  }

  /** Displays the aquariums. */
  public void displayAquariums() {
    // TODO 11 - complete
    int gridSize = BOXSIZE * size;
    // Draw around entire grid, lines are 3 pixels wide
    sc.drawRectangle(OFFSET - 1, OFFSET - 1, OFFSET + gridSize + 1, OFFSET + 2, altColor); // top
    sc.drawRectangle(OFFSET - 1, OFFSET - 1, OFFSET + 2, OFFSET + gridSize + 1, altColor); // left
    sc.drawRectangle(
        OFFSET - 1,
        OFFSET + gridSize - 2,
        OFFSET + gridSize + 1,
        OFFSET + gridSize + 1,
        altColor); // bottom
    sc.drawRectangle(
        OFFSET + gridSize - 2,
        OFFSET - 1,
        OFFSET + gridSize + 1,
        OFFSET + gridSize + 1,
        altColor); // right

    int[][] aquariums = puzzle.getAquariums();
    // Draw red line on bottom or right depending on group
    for (int x = 0; x < size; x++) {
      for (int y = 0; y < size; y++) {
        int group = aquariums[x][y];
        if ((x + 1 < size) && (group != aquariums[x + 1][y])) { // Draw right border
          sc.drawRectangle(
              OFFSET + y * BOXSIZE - 1,
              OFFSET + (x + 1) * BOXSIZE - 2,
              OFFSET + (y + 1) * BOXSIZE + 1,
              OFFSET + (x + 1) * BOXSIZE + 2,
              altColor);
        }
        if ((y + 1 < size) && (group != aquariums[x][y + 1])) { // Draw bottom border
          sc.drawRectangle(
              OFFSET + (y + 1) * BOXSIZE - 1,
              OFFSET + x * BOXSIZE - 1,
              OFFSET + (y + 1) * BOXSIZE + 2,
              OFFSET + (x + 1) * BOXSIZE + 1,
              altColor);
        }
      }
    }
  }

  /** Displays the buttons below the grid. */
  public void displayButtons() {
    // TODO 12 - complete
    sc.setFont(new Font("Consolas", 1, 20));

    sc.drawRectangle(0, WINDOWSIZE - BOXSIZE, WINDOWSIZE / 2, WINDOWSIZE, Color.green);
    sc.drawString("CHECK", (WINDOWSIZE / 4) - 32, WINDOWSIZE - (BOXSIZE / 2) + 8, Color.black);

    sc.drawRectangle(WINDOWSIZE / 2, WINDOWSIZE - BOXSIZE, WINDOWSIZE, WINDOWSIZE, Color.red);
    sc.drawString("CLEAR", (WINDOWSIZE / 4) * 3 - 32, WINDOWSIZE - (BOXSIZE / 2) + 8, Color.black);

    if (puzzle.getSize() < solvePuzzleSize) {
      sc.drawRectangle(WINDOWSIZE - (BOXSIZE * 3), 0, WINDOWSIZE - BOXSIZE, BOXSIZE, Color.green);
      sc.drawString(
          "SOLVE", WINDOWSIZE - (BOXSIZE * 3) + (BOXSIZE / 4), (BOXSIZE / 4) * 3 - 4, Color.black);
    }

    // Draw dark mode toggle
    sc.drawRectangle(WINDOWSIZE - BOXSIZE, 0, WINDOWSIZE, BOXSIZE, Color.cyan);

    sc.setFont(new Font("Segoe UI Symbol", 1, 20));
    String themeButton = "\u263E"; // Moon symbol
    if (darkMode) {
      themeButton = "\u263C"; // Sun symbol
    }
    sc.drawString(themeButton, WINDOWSIZE - (BOXSIZE / 4) * 3, (BOXSIZE / 4) * 3 - 4, Color.black);
    // Draw new puzzle button
    sc.drawRectangle(WINDOWSIZE - BOXSIZE, BOXSIZE, WINDOWSIZE, 2 * BOXSIZE, Color.green);
    sc.setFont(new Font("Segoe UI Symbol", 1, 30));
    String newPuzzleButton = "+";
    sc.drawString(
        newPuzzleButton, WINDOWSIZE - (BOXSIZE / 4) * 3, BOXSIZE + (BOXSIZE / 4) * 3, Color.black);
  }

  /**
   * Updates the display of Square r,c. Sets the display of this square to whatever is in the
   * squares array.
   */
  public void updateSquare(int r, int c) {
    // TODO 14 - complete
    Color material;
    Space square = puzzle.getSpaces()[r][c];
    if (square == Space.WATER) {
      material = waterColor;
    } else {
      material = backColor;
    }

    sc.drawRectangle(
        OFFSET + (BOXSIZE * c),
        OFFSET + (BOXSIZE * r),
        OFFSET + (BOXSIZE * (c + 1)),
        OFFSET + (BOXSIZE * (r + 1)),
        material);
    if (square == Space.AIR) {
      sc.drawDisc(
          OFFSET + (BOXSIZE * c) + (BOXSIZE / 2),
          OFFSET + (BOXSIZE * r) + (BOXSIZE / 2),
          BOXSIZE / 4,
          altColor);
      sc.drawDisc(
          OFFSET + (BOXSIZE * c) + (BOXSIZE / 2),
          OFFSET + (BOXSIZE * r) + (BOXSIZE / 2),
          BOXSIZE / 6,
          backColor);
    }
  }
  /**
   * Toggles dark mode on and off. Updates colors and redraws canvas elements. Clears puzzle
   * arrangement as to not cause errors.
   */
  public void toggleDarkMode() {
    if (!darkMode) {
      foreColor = new Color(245, 245, 245);
      backColor = new Color(20, 25, 50);
      waterColor = new Color(45, 130, 200);
      altColor = new Color(180, 45, 45);

    } else {
      foreColor = Color.black;
      backColor = Color.white;
      waterColor = Color.blue;
      altColor = Color.red;
    }
    darkMode = !darkMode;
    sc.drawRectangle(0, 0, WINDOWSIZE, WINDOWSIZE, backColor); // redraw background
    Space[][] spaces = puzzle.getSpaces();
    for (int c = 0; c < spaces.length; c++) {
      for (int r = 0; r < spaces[0].length; r++) {
        updateSquare(c, r);
      }
    }
    displayPuzzle();
  }

  public void createNewPuzzle() {
    puzzle = CheckSolution.newPuzzle(size);
    sc.drawRectangle(0, 0, WINDOWSIZE, WINDOWSIZE, backColor);
    this.displayPuzzle();
    startTime = clock.instant();
  }

  /**
   * Responds to a mouse click. If it's on the board, make the appropriate move and update the
   * screen display. If it's on SOLVED?, check the solution and display the result. If it's on
   * CLEAR, clear the puzzle and update the screen display.
   */
  public void mousePressed(MouseEvent e) {
    // TODO 15 - complete
    int x = e.getX();
    int y = e.getY();
    // If true, click was inside the grid
    if ((OFFSET < x && x < WINDOWSIZE - OFFSET) && (OFFSET < y && y < WINDOWSIZE - OFFSET)) {
      int c = (x - OFFSET) / BOXSIZE;
      int r = (y - OFFSET) / BOXSIZE;

      if (e.getButton() == MouseEvent.BUTTON1) {
        puzzle.leftClick(r, c);
      }
      if (e.getButton() == MouseEvent.BUTTON3) {
        puzzle.rightClick(r, c);
      }

      this.updateSquare(r, c);
      this.displayGrid();
      this.displayAquariums();
    } else if (WINDOWSIZE - OFFSET / 2 < y) { // bottom buttons
      // If true check button pressed, else clear button pressed
      if (x < WINDOWSIZE / 2) { // left button
        String solveText = CheckSolution.isSolution(puzzle);

        sc.setFont(new Font("Segoe UI Symbol", 1, 20));
        // Redraw background
        sc.drawRectangle(0, 0, WINDOWSIZE, WINDOWSIZE, backColor);
        sc.drawString(solveText, 32, WINDOWSIZE - (BOXSIZE / 2) - BOXSIZE + 8, foreColor);
        if (solveText.equals("\u2713\u2713\u2713")) { // if correct
          solveTime = Duration.between(startTime, clock.instant());
          sc.setFont(new Font("Consolas", 1, 15));
          sc.drawString("Solved in ", BOXSIZE, BOXSIZE / 2 - 4, foreColor);
          sc.drawString(
              solveTime.getSeconds() + " seconds. ", BOXSIZE, BOXSIZE / 2 + 12, foreColor);
          startTime = clock.instant();
        }
        // Redraw other elements
        this.displayPuzzle();
      } else { // if right button
        puzzle.clear();
        sc.drawRectangle(0, 0, WINDOWSIZE, WINDOWSIZE, backColor);
        this.displayPuzzle();
        startTime = clock.instant();
      }
    } else if (OFFSET / 2 > y) { // top buttons
      if (x > WINDOWSIZE - BOXSIZE) { // dark mode toggle
        toggleDarkMode();
      } else if (x < WINDOWSIZE - BOXSIZE && x > WINDOWSIZE - (BOXSIZE * 4)) {
        if (puzzle.getSize() < solvePuzzleSize) {
          startTime = clock.instant();
          System.out.println("Solving puzzle...");
          puzzle.setSolution();
          // CheckSolution.solve(puzzle);

          sc.drawRectangle(0, 0, WINDOWSIZE, WINDOWSIZE, backColor);

          this.displayPuzzle();
        }
      }
    } else if (OFFSET > y) {
      if (x > WINDOWSIZE - BOXSIZE) { // new puzzle button
        createNewPuzzle();
      }
    }
  }

  public void mouseClicked(MouseEvent e) {}

  public void mouseReleased(MouseEvent e) {}

  public void mouseEntered(MouseEvent e) {}

  public void mouseExited(MouseEvent e) {}
}
