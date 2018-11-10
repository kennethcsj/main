package seedu.address.ui;

import static org.junit.Assert.assertEquals;
import static seedu.address.testutil.EventsUtil.postNow;
import static seedu.address.testutil.TypicalBooks.getTypicalBooks;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_BOOK;
import static seedu.address.ui.testutil.GuiTestAssert.assertCardDisplaysBook;
import static seedu.address.ui.testutil.GuiTestAssert.assertCardEquals;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import guitests.guihandles.BookCardHandle;
import guitests.guihandles.BookListPanelHandle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.commons.events.ui.JumpToListRequestEvent;
import seedu.address.commons.util.FileUtil;
import seedu.address.commons.util.XmlUtil;
import seedu.address.model.book.Book;
import seedu.address.storage.XmlSerializableBookInventory;

public class BookListPanelTest extends GuiUnitTest {
    private static final ObservableList<Book> TYPICAL_BOOKS =
            FXCollections.observableList(getTypicalBooks());

    private static final JumpToListRequestEvent JUMP_TO_SECOND_EVENT = new JumpToListRequestEvent(INDEX_SECOND_BOOK);

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "sandbox");

    private static final long CARD_CREATION_AND_DELETION_TIMEOUT = 2500;

    private BookListPanelHandle bookListPanelHandle;

    @Test
    public void display() {
        initUi(TYPICAL_BOOKS);

        for (int i = 0; i < TYPICAL_BOOKS.size(); i++) {
            bookListPanelHandle.navigateToCard(TYPICAL_BOOKS.get(i));
            Book expectedBook = TYPICAL_BOOKS.get(i);
            BookCardHandle actualCard = bookListPanelHandle.getBookCardHandle(i);

            assertCardDisplaysBook(expectedBook, actualCard);
            assertEquals(Integer.toString(i + 1) + ". ", actualCard.getId());
        }
    }

    @Test
    public void handleJumpToListRequestEvent() {
        initUi(TYPICAL_BOOKS);
        postNow(JUMP_TO_SECOND_EVENT);
        guiRobot.pauseForHuman();

        BookCardHandle expectedBook = bookListPanelHandle.getBookCardHandle(INDEX_SECOND_BOOK.getZeroBased());
        BookCardHandle selectedBook = bookListPanelHandle.getHandleToSelectedCard();
        assertCardEquals(expectedBook, selectedBook);
    }

    /**
     * Verifies that creating and deleting large number of books in {@code BookListPanel} requires lesser than
     * {@code CARD_CREATION_AND_DELETION_TIMEOUT} milliseconds to execute.
     */
    /*
    @Test
    public void performanceTest() throws Exception {
        ObservableList<Book> backingList = createBackingList(10000);

        assertTimeoutPreemptively(ofMillis(CARD_CREATION_AND_DELETION_TIMEOUT), () -> {
            initUi(backingList);
            guiRobot.interact(backingList::clear);
        }, "Creation and deletion of book cards exceeded time limit");
    }
    */
    /**
     * Returns a list of books containing {@code bookCount} books that is used to populate the
     * {@code BookListPanel}.
     */
    private ObservableList<Book> createBackingList(int bookCount) throws Exception {
        Path xmlFile = createXmlFileWithBooks(bookCount);
        XmlSerializableBookInventory xmlAddressBook =
                XmlUtil.getDataFromFile(xmlFile, XmlSerializableBookInventory.class);
        return FXCollections.observableArrayList(xmlAddressBook.toModelType().getBookList());
    }

    /**
     * Returns a .xml file containing {@code bookCount} books. This file will be deleted when the JVM terminates.
     */
    private Path createXmlFileWithBooks(int bookCount) throws Exception {
        StringBuilder builder = new StringBuilder();
        builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
        builder.append("<addressbook>\n");
        for (int i = 0; i < bookCount; i++) {
            builder.append("<persons>\n");
            builder.append("<name>").append(i).append("a</name>\n");
            builder.append("<phone>000</phone>\n");
            builder.append("<email>a@aa</email>\n");
            builder.append("<address>a</address>\n");
            builder.append("</persons>\n");
        }
        builder.append("</addressbook>\n");

        Path manyBooksFile = Paths.get(TEST_DATA_FOLDER + "manyPersons.xml");
        FileUtil.createFile(manyBooksFile);
        FileUtil.writeToFile(manyBooksFile, builder.toString());
        manyBooksFile.toFile().deleteOnExit();
        return manyBooksFile;
    }

    /**
     * Initializes {@code bookListPanelHandle} with a {@code BookListPanel} backed by {@code backingList}.
     * Also shows the {@code Stage} that displays only {@code BookListPanel}.
     */
    private void initUi(ObservableList<Book> backingList) {
        BookListPanel bookListPanel = new BookListPanel(backingList);
        uiPartRule.setUiPart(bookListPanel);

        bookListPanelHandle = new BookListPanelHandle(getChildNode(bookListPanel.getRoot(),
                BookListPanelHandle.BOOK_LIST_VIEW_ID));
    }
}
