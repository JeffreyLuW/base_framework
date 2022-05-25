const wins = [];
let initZIndex = 1000;

export function pushWin(win) {
  wins.push(win);
  //设置索引
  win.zIndex = winCurrMaxZindex() + 2;
}

export function setWinStyleZIndex(win) {
  if (win._setZindex) return;
  if (win.$refs.root) {
    win.$refs.root.style.zIndex = win.zIndex;
    win._setZindex = true;
  }
}

export function removeWin(win) {
  let index = wins.indexOf(win);
  if (index === -1) return;
  wins.splice(index, 1);
}

export function winCurrMaxZindex() {
  let maxIndex = initZIndex;
  wins.forEach((win) => {
    maxIndex = Math.max(win.zIndex || 0, maxIndex);
  });
  return maxIndex;
}

export function moveTop(win) {
  if (wins[wins.length - 1] === win) {
    return;
  }
  wins.splice(wins.indexOf(win), 1);
  wins.push(win);
  wins.forEach((v, i) => {
    v.zIndex = initZIndex + i*2;
    if (v.$refs.root) v.$refs.root.style.zIndex = v.zIndex;
  });
}
export const AllWindows=wins;
