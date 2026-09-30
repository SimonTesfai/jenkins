package hudson.scm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import hudson.model.User;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Test;

class ChangeLogSetEntryTest {

    /** Minimal entry that only implements the abstract methods, like an older SCM plugin would. */
    private static class LegacyEntry extends ChangeLogSet.Entry {
        private final String msg;

        LegacyEntry(String msg) {
            this.msg = msg;
        }

        @Override
        public String getMsg() {
            return msg;
        }

        @Override
        public User getAuthor() {
            return null;
        }

        @Override
        public Collection<String> getAffectedPaths() {
            return List.of();
        }
    }

    /** Entry that distinguishes summary from full message, like the Git plugin. */
    private static class FullMessageEntry extends LegacyEntry {
        private final String comment;

        FullMessageEntry(String msg, String comment) {
            super(msg);
            this.comment = comment;
        }

        @Override
        public String getComment() {
            return comment;
        }
    }

    @Test
    void getCommentFallsBackToGetMsg() {
        ChangeLogSet.Entry entry = new LegacyEntry("Fix login bug");
        assertEquals("Fix login bug", entry.getComment());
    }

    @Test
    void getCommentCanBeOverriddenToExposeFullMessage() {
        ChangeLogSet.Entry entry = new FullMessageEntry(
                "Fix login bug",
                "Fix login bug\n\nResolves PROJ-1234.");
        assertEquals("Fix login bug", entry.getMsg());
        assertEquals("Fix login bug\n\nResolves PROJ-1234.", entry.getComment());
        assertTrue(entry.getComment().contains("PROJ-1234"));
    }

    @Test
    void getCommentEscapedEscapesHtml() {
        ChangeLogSet.Entry entry = new FullMessageEntry("summary", "a <b> tag");
        assertTrue(entry.getCommentEscaped().contains("&lt;b&gt;"));
    }
}
