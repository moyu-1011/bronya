package moe.moyu.util;

import moe.moyu.cache.MessageDuplicate;
import moe.moyu.constant.Constant;
import moe.moyu.entity.Image;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.message.data.ForwardMessageBuilder;

import java.util.List;

public class MessageSender {

    public static void sendForwardMessage(User user, List<Image> imageList) {
        boolean isImageEmpty = imageList == null || imageList.size() < 1;
        if (isImageEmpty) {
            user.sendMessage(Constant.STORAGE_NOT_FOUND);
        } else if (imageList.size() == 1) {
            user.sendMessage(net.mamoe.mirai.message.data.Image.fromId(imageList.get(0).getImageId()));
        } else {
            ForwardMessageBuilder chainBuilder = new ForwardMessageBuilder(user);
            for (Image image : imageList)
                chainBuilder.add(user, net.mamoe.mirai.message.data.Image.fromId(image.getImageId()));
            user.sendMessage(chainBuilder.build());
        }

        if (!isImageEmpty) {
            // todo: 暂时实现
            MessageDuplicate.copy(imageList);
        }
    }


}
