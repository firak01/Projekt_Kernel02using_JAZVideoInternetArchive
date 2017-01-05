package use.via.client.module.export;

import java.util.ArrayList;

import basic.zKernelUI.component.model.EventComponentSelectionResetZZZ;
import basic.zKernelUI.component.model.IListenerSelectionResetZZZ;

public interface ISenderFileChangedVIA {
	public abstract void fireEvent(EventListFileSelectedVIA event);

	public abstract void removeListenerFileSelection(IListenerFileSelectedVIA l);

	public abstract void addListenerFileSelection(IListenerFileSelectedVIA l);
	
	public abstract ArrayList getListenerRegisteredAll();
}
