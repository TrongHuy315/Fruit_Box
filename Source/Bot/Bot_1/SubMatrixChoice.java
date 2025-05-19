package Source.Bot.Bot_1;

public class SubMatrixChoice {
    private int r1, c1, r2, c2;
    private int nonZeroElements;

    public SubMatrixChoice(int r1, int c1, int r2, int c2, int nonZeroElements) {
        this.r1 = r1;
        this.c1 = c1;
        this.r2 = r2;
        this.c2 = c2;
        this.nonZeroElements = nonZeroElements;
    }

    int getR1() {
        return r1;
    }

    int getR2() {
        return r2;
    }

    int getC1() {
        return c1;
    }

    int getC2() {
        return c2;
    }

    int getNonZeroElements() {
        return nonZeroElements;
    }
}
