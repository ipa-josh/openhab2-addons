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

import com.connectsdk.core.ProgramList;
import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.service.capability.TVControl;
import com.connectsdk.service.capability.TVControl.ProgramListListener;
import com.connectsdk.service.command.ServiceCommandError;
import com.connectsdk.service.command.ServiceSubscription;

/**
 * Outputs the current program info from TV
 *
 * @author Joshua Hampp - initial contribution
 */
public class TVProgramList extends BaseChannelHandler<ProgramListListener> {
    private final Logger logger = LoggerFactory.getLogger(TVProgramList.class);

    private TVControl getControl(ConnectableDevice device) {
        return device.getCapability(TVControl.class);
    }

    @Override
    public void onReceiveCommand(ConnectableDevice device, String channelId, LGWebOSHandler handler, Command command) {
    }

    @Override
    protected Optional<ServiceSubscription<ProgramListListener>> getSubscription(ConnectableDevice device, String channelId,
            LGWebOSHandler handler) {
        if (device.hasCapability(TVControl.Program_List)) {
            return Optional.of(getControl(device).subscribeProgramList(new ProgramListListener() {

                @Override
                public void onError(ServiceCommandError error) {
                    logger.debug("error: {} {} {}", error.getCode(), error.getPayload(), error.getMessage());
                }

                @Override
                public void onSuccess(ProgramList programList) {
                    handler.postUpdate(channelId, new StringType(programList.toJSONObject().toString()));
                }
            }));
        } else {
            return null;
        }
    }
}
