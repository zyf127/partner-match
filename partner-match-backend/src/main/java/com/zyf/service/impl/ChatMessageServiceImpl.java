package com.zyf.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyf.model.domain.ChatMessage;
import com.zyf.service.ChatMessageService;
import com.zyf.mapper.ChatMessageMapper;
import org.springframework.stereotype.Service;

/**
* @author zyf
*/
@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage>
    implements ChatMessageService{

}




