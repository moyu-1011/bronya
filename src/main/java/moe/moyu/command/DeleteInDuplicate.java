package moe.moyu.command;

import moe.moyu.cache.MessageDuplicate;
import moe.moyu.dao.ImageDao;
import moe.moyu.entity.Image;
import net.mamoe.mirai.contact.User;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 删除: 通过副本索引
 */
public class DeleteInDuplicate extends Command{
    public DeleteInDuplicate(ImageDao imageDao) {
        super(imageDao);
    }

    @Override
    CommandType getCommandType() {
        return CommandType.DELETE_BY_INDEX;
    }

    @Override
    public void execute(@Nullable User user, @Nullable List<Image> imageList, String keyword) {
        int deleteIndex = Integer.parseInt(keyword);
        boolean isLegalCommandIndex = MessageDuplicate.setDeleteIndex(deleteIndex);
        if (isLegalCommandIndex){
            Long messageId = MessageDuplicate.getMessageId();
            imageDao.delete(messageId);
        }
    }
}
