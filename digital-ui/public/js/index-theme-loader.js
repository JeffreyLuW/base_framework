(function () {
  var theme = null;
  if (window.__theme_color) {
    var themeSettings = localStorage.getItem('__util__xqh____store__settings');
    if (themeSettings) {
      themeSettings = JSON.parse(themeSettings);
      theme = themeSettings && themeSettings.val && themeSettings.val.theme;
    }
  } else {
    var themeSettings = localStorage.getItem('__ThemeTemplate__color_vars_bgdark');
    if (themeSettings) {
      themeSettings = JSON.parse(themeSettings);
      theme = themeSettings && themeSettings.val && themeSettings.val;
    }
  }
  var loaderSection = '';
  //默认主题色
  if (!theme) {
    theme = window.__union_theme;
  }
  loaderSection += '<div class="loader-section section-left" style="background:' + theme + ';"></div>';
  loaderSection += '<div class="loader-section section-right" style="background:' + theme + ';"></div>';
  document.write(loaderSection);
})();
