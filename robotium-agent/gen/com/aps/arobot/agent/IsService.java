/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\darren\\workspace-CV\\robotium-remote\\robotium-agent\\src\\com\\aps\\arobot\\agent\\IsService.aidl
 */
package com.aps.arobot.agent;
public interface IsService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.aps.arobot.agent.IsService
{
private static final java.lang.String DESCRIPTOR = "com.aps.arobot.agent.IsService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.aps.arobot.agent.IsService interface,
 * generating a proxy if needed.
 */
public static com.aps.arobot.agent.IsService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.aps.arobot.agent.IsService))) {
return ((com.aps.arobot.agent.IsService)iin);
}
return new com.aps.arobot.agent.IsService.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_getMessage:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getMessage();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_output:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.output(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.aps.arobot.agent.IsService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public java.lang.String getMessage() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getMessage, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void output(java.lang.String s) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(s);
mRemote.transact(Stub.TRANSACTION_output, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_getMessage = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_output = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
public java.lang.String getMessage() throws android.os.RemoteException;
public void output(java.lang.String s) throws android.os.RemoteException;
}
