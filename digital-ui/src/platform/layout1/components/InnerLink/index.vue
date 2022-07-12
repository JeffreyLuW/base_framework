<!--<template>
  <div>
    <h6>innerLink</h6>
  </div>
</template>-->
<script>
export default {
  data() {
    return {};
  },
  render() { 
    console.log('render invoke:' + this.$route.query.url);
    let url = this.$route.query.url;
    if (url === "") {
      return "404";
    }
    const style = { position: 'absolute', left:0, right: 0, top: 0, bottom: 0 };
    return (
      <div style={style}>
        <iframe
          src={url}
          frameborder="no"
          style="width: 100%; height: 100%"
          scrolling="auto"
        ></iframe>
      </div>
    );
  },
};
</script>
