package com.elementtimes.elementcore.api.book;

import com.elementtimes.elementcore.ElementCore;
import com.elementtimes.elementcore.api.annotation.ModContainer;
import com.elementtimes.elementcore.api.annotation.part.Method2;
import com.elementtimes.elementcore.api.book.screen.DrawStage;
import com.elementtimes.elementcore.api.book.screen.IContent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;

@ModContainer(screen = @Method2("com.elementtimes.elementcore.api.book.screen.BookGuiContainer"))
public class BookContainer extends Container {

    protected IBook mBook;
    protected int mPage = 0;
    protected int xLength = 215;
    protected int yLength = 197;

    public BookContainer(int id, PlayerInventory inventory) {
        this(id, ((ItemBook) ItemBook.ITEM.getItem()).getBook());
    }

    public BookContainer(int id, IBook book) {
        super(ElementCore.CONTAINER.elements.generatedContainerTypes.get(BookContainer.class), id);
        mBook = book;
        if (getTotalPage() == 0) {
            mBook.getPages().add(new Page(mBook));
        }
        // draw
        int mouseX = 0;
        int mouseY = 0;
        Page page = getPage();
        List<IContent> contents = page.getContents(DrawStage.CONTAINER);
        Iterator<IContent> iterator = contents.iterator();
        boolean draw = true;
        int yLast = 0;
        int ySpace = yLength;
        while (iterator.hasNext()) {
            IContent content = iterator.next();
            content.setContainer(this);
            if (draw) {
                // draw
                int newY = content.draw(0, yLast, xLength, yLength, mouseX, mouseY);
                int height = newY - yLast;
                ySpace -= height;
                yLast = newY;
                // replace
                if (ySpace <= 0) {
                    draw = false;
                    newPage().addAll(content.split());
                }
                IContent afterDisplay = content.replaceAfterDisplay();
                if (afterDisplay == null) {
                    iterator.remove();
                } else {
                    page.replace(content, afterDisplay);
                }
            } else {
                newPage().add(content);
            }
        }
    }

    public IBook getBook() {
        return mBook;
    }

    public int getTotalPage() {
        return getBook().getPages().size();
    }

    public int getIndex() {
        jumpTo(mPage);
        return mPage;
    }

    public Page getPage() {
        return getBook().getPages().get(getIndex());
    }

    public Page getPage(int index) {
        if (index < 0) {
            return getBook().getPages().get(0);
        } else if (index >= getTotalPage()) {
            return getBook().getPages().get(getTotalPage() - 1);
        }
        return getBook().getPages().get(getIndex());
    }

    public void nextPage() {
        jumpTo(getIndex() + 1);
    }

    public void prevPage() {
        jumpTo(getIndex() - 1);
    }

    public void jumpTo(int page) {
        mPage = Math.min(Math.max(0, page), getTotalPage() - 1);
    }

    @Override
    public Slot addSlot(Slot slotIn) {
        return super.addSlot(slotIn);
    }

    @Override
    public boolean canInteractWith(@Nonnull PlayerEntity playerIn) {
        return true;
    }

    public Page newPage() {
        Page head = getPage().getPageHead();
        Page page = getPage().newTempPage();
        int totalPage = getTotalPage();
        int i = getIndex();
        while (i < totalPage && getPage(i).getPageHead() == head) {
            i++;
        }
        mBook.getPages().add(Math.min(i, totalPage), page);
        return page;
    }

    @Override
    public void onContainerClosed(@Nonnull PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        mBook.resume();
    }
}
