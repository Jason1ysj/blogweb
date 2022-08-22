// reference: https://dev.to/igorovic/simplest-way-to-persist-redux-state-to-localstorage-e67

// localStorage.js
import {appConfig} from "../config";

const KEY='redux'

export const loadState = () => {
  try {
    const serializedState = localStorage.getItem(KEY);
    if (serializedState === null) {
      return undefined;
    }

    const item = JSON.parse(serializedState);
    console.log("load state", item)
    if (new Date().getTime() > item.expiry) {
      localStorage.removeItem(KEY);
      return undefined;
    }
    return item.state;
  } catch (err) {
    return undefined;
  }
};

// localStorage.js
export const saveState = (state) => {
  try {
    const item = {
      state: state,
      expiry: new Date().getTime() + appConfig.pageStaleTimeInMS
    };
    console.log("save state: ", item)
    localStorage.setItem(KEY, JSON.stringify(item));

    localStorage.setItem('token', JSON.stringify(state.userReducer.token));
  } catch {
    // ignore write errors
  }
};