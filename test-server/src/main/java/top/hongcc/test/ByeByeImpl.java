package top.hongcc.test;

import top.hongcc.rpc.annotation.Service;
import top.hongcc.rpc.api.ByeByeService;

/**
 * description: ByeByeImpl
 * author: hcc
 */
@Service
public class ByeByeImpl implements ByeByeService {

    @Override
    public String bye(String str) {
        return "bye bye " + str;
    }

}
