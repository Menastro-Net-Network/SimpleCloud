/*
 * MIT License
 *
 * Copyright (C) 2020 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.api.extension

import eu.thesimplecloud.api.utils.IAuthenticatable
import eu.thesimplecloud.api.wrapper.IWrapperInfo
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.combineAllPromises
import eu.thesimplecloud.clientserverapi.server.client.clientmanager.IClientManager
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClient
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClientValue

fun IClientManager<*>.getAllAuthenticatedClients(): List<IConnectedClient<out IConnectedClientValue>> {
    return this.getClients().filter { it.getClientValue() != null && (it.getClientValue() as IAuthenticatable).isAuthenticated() }
}

fun IClientManager<*>.sendPacketToAllAuthenticatedClients(packet: IPacket): ICommunicationPromise<Unit> {
    return this.getAllAuthenticatedClients().map { it.sendUnitQuery(packet) }.combineAllPromises()
}

fun IClientManager<*>.sendPacketToAllAuthenticatedNonWrapperClients(packet: IPacket) {
    this.getAllAuthenticatedClients().filter { it.getClientValue() !is IWrapperInfo }.forEach { it.sendUnitQuery(packet) }
}

fun IClientManager<*>.sendPacketToAllAuthenticatedWrapperClients(packet: IPacket) {
    this.getAllAuthenticatedClients().filter { it.getClientValue() is IWrapperInfo }.forEach { it.sendUnitQuery(packet) }
}
