const ORIGINAL_THEME = '#409EFF' // default color
//用于主题色快速切换
export default {
  chalk: '', // content of theme-chalk css
  theme: '',
  /**
   * 应用新的主题颜色
   * @param val color   #409EFF
   * @return {Promise<void>}
   */
  async applyTheme(val) {
    const oldVal = this.chalk ? this.theme : ORIGINAL_THEME;
    if (!val || val === oldVal || typeof val !== 'string') return;
    const themeCluster = this.getThemeCluster(val.replace('#', ''))
    const originalCluster = this.getThemeCluster(oldVal.replace('#', ''))

    const getHandler = (variable, id) => {
      return () => {
        const originalCluster = this.getThemeCluster(ORIGINAL_THEME.replace('#', ''))
        const newStyle = this.updateStyle(this[variable], originalCluster, themeCluster)

        let styleTag = document.getElementById(id)
        if (!styleTag) {
          styleTag = document.createElement('style')
          styleTag.setAttribute('id', id)
          document.head.appendChild(styleTag)
        }
        styleTag.innerText = newStyle
      }
    }

    if (!this.chalk) {
      // let url = `https://unpkg.com/element-ui@${version}/lib/theme-chalk/index.css`
      //修改为本地精简的css请求
      // url=this.$util.baseUrl()+"static/css/element-theme-template.css";
      let url = process.env.BASE_URL + "css/element_theme_simple.css";
      await this.getCSSString(url, 'chalk')
    }

    const chalkHandler = getHandler('chalk', 'chalk-style')
    chalkHandler()
    // const reg = /.theme-flag-selector/;
    // const styles = [].slice.call(document.querySelectorAll('style'))
    //   .filter(style => {
    //     const text = style.innerText
    //     return reg.test(text) && new RegExp(oldVal, 'i').test(text);
    //     // return new RegExp(oldVal, 'i').test(text) && !/Chalk Variables/.test(text)
    //   })
    // //.theme-flag-selector
    // console.log("styles", styles);
    // styles.forEach(style => {
    //   const {innerText} = style
    //   if (typeof innerText !== 'string') return
    //   style.innerText = this.updateStyle(innerText, originalCluster, themeCluster)
    // })
  },
  //样式字符串 替换颜色
  updateStyle(style, oldCluster, newCluster) {
    let newStyle = style
    oldCluster.forEach((color, index) => {
      newStyle = newStyle.replace(new RegExp(color, 'ig'), newCluster[index])
    })
    return newStyle
  },
  //请求样式模板  url=this.$util.baseUrl()+"static/css/element_theme_simple.css";
  //variable='chalk'
  getCSSString(url, variable) {
    return new Promise(resolve => {
      const xhr = new XMLHttpRequest()
      xhr.onreadystatechange = () => {
        if (xhr.readyState === 4 && xhr.status === 200) {
          this[variable] = xhr.responseText.replace(/@font-face{[^}]+}/, '')
          resolve()
        }
      }
      xhr.open('GET', url)
      xhr.send()
    })
  },
  //计算主题相关颜色数组
  getThemeCluster(theme) {
    const tintColor = (color, tint) => {
      let red = parseInt(color.slice(0, 2), 16)
      let green = parseInt(color.slice(2, 4), 16)
      let blue = parseInt(color.slice(4, 6), 16)

      if (tint === 0) { // when primary color is in its rgb space
        return [red, green, blue].join(',')
      } else {
        red += Math.round(tint * (255 - red))
        green += Math.round(tint * (255 - green))
        blue += Math.round(tint * (255 - blue))

        red = red.toString(16)
        green = green.toString(16)
        blue = blue.toString(16)

        return `#${red}${green}${blue}`
      }
    }

    const shadeColor = (color, shade) => {
      let red = parseInt(color.slice(0, 2), 16)
      let green = parseInt(color.slice(2, 4), 16)
      let blue = parseInt(color.slice(4, 6), 16)

      red = Math.round((1 - shade) * red)
      green = Math.round((1 - shade) * green)
      blue = Math.round((1 - shade) * blue)

      red = red.toString(16)
      green = green.toString(16)
      blue = blue.toString(16)

      return `#${red}${green}${blue}`
    }

    const clusters = [theme]
    for (let i = 0; i <= 9; i++) {
      clusters.push(tintColor(theme, Number((i / 10).toFixed(2))))
    }
    clusters.push(shadeColor(theme, 0.1))
    return clusters
  }
}
