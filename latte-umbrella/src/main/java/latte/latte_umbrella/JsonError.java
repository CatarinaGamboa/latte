package latte.latte_umbrella;

public class JsonError {
    private int startLine;
    private int startColumn;
    private int endLine;
    private int endColumn;
    private String shortMessage;
    private String fullMessage;

    public JsonError(int startLine, int startColumn, int endLine, int endColumn, String fullMessage, String shortMessage) {
        this.startLine = startLine;
        this.startColumn = startColumn;
        this.endLine = endLine;
        this.endColumn = endColumn;
        this.shortMessage = shortMessage;
        this.fullMessage = fullMessage;
    }

    // Getters and setters (optional if you're using a serialization library)
    public int getStartLine() {
        return startLine;
    }

    public int getStartColumn() {
        return startColumn;
    }

    public int getEndLine() {
        return endLine;
    }

    public int getEndColumn() {
        return endColumn;
    }

    public String getShortMessage() {
        return shortMessage;
    }

    public String getFullMessage() {
        return fullMessage;
    }
}