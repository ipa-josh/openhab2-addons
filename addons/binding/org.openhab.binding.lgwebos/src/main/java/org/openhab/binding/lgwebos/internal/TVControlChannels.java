/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.lgwebos.internal;

import java.util.List;
import java.util.Optional;

import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.lgwebos.handler.LGWebOSHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONObject;

import com.connectsdk.core.ChannelInfo;
import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.service.capability.TVControl;
import com.connectsdk.service.capability.TVControl.ChannelListListener;
import com.connectsdk.service.command.ServiceCommandError;
import com.connectsdk.service.command.ServiceSubscription;


/**
 * Handles TV Control Channel State. This is read only.
 * Subscribes to to current channel name.
 *
 * @author Sebastian Prehn - initial contribution
 */
public class TVControlChannels extends BaseChannelHandler<ChannelListListener> {
    private final Logger logger = LoggerFactory.getLogger(TVControlChannels.class);

    private TVControl getControl(ConnectableDevice device) {
        return device.getCapability(TVControl.class);
    }

    @Override
    public void onReceiveCommand(ConnectableDevice device, String channelId, LGWebOSHandler handler, Command command) {
    }

    @Override
    protected Optional<ServiceSubscription<ChannelListListener>> getSubscription(ConnectableDevice device, String channelId,
            LGWebOSHandler handler) {

        if (device.hasCapability(TVControl.Channel_List)) {
            final TVControl control = getControl(device);
            control.getChannelList(new ChannelListListener() {

                @Override
                public void onError(ServiceCommandError error) {
                    logger.debug("error: {} {} {}", error.getCode(), error.getPayload(), error.getMessage());
                }

                @Override
                public void onSuccess(List<ChannelInfo> channels) {
                    JSONObject obj = new JSONObject();

                    channels.forEach(c ->  obj.put(c.getNumber(), c.getName()));
                    handler.postUpdate(channelId, new StringType(obj.toString()));
                }
            });
        }

        return null;
    }
}
