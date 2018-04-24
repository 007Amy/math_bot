<template>
  <div id="dom-counter" class="_flex" data-ng-init="init()">

    <div class="side-bar left-side-bar _flex _flex-column">

      <div class="bucket cat-bucket _single-flex _flex _flex-column"></div>

      <div class="bucket ten-bucket _single-flex _flex _flex-column"></div>

      <div class="bucket one-hundred-bucket _single-flex _flex _flex-column"></div>

      <div class="bucket one-thousand-bucket _single-flex _flex _flex-column"></div>

      <div class="bucket ten-thousand-bucket _single-flex _flex _flex-column"></div>

      <div class="bucket staging-tool-red-bucket _single-flex _flex _flex-column"></div>

      <div class="bucket staging-tool-green-bucket _single-flex _flex _flex-column"></div>

      <div class="bucket staging-tool-yellow-bucket _single-flex _flex _flex-column"></div>

      <div class="tool-bucket trash-bucket _single-flex _flex _flex-column animated">
        <div class="trash _flex animated tada"></div>
      </div>
    </div>

    <div class="drop-area _flex"></div>
    <button type="button" class="profile btn btn-info btn-block animated bounceInRight" @click="goToProfile()">
      Profile
    </button>
    <!-- <div class="side-bar right-side-bar _flex _flex-column animated bounceInRight">
    </div> -->
  </div>
</template>
<script>
  import Robot from './Robot';
  import {_} from 'underscore';
  import {draggable} from 'vuedraggable';

  export default {
    mounted() {
      window.dropped = this.dropped;
      // Gathers all the children from the drop area (tools)
      // escapes them then stores the html to local storage
      // to maintain state.
      // window.onbeforeunload = (e) => {
      //   let div = $('<div></div>');
      //   $('.drop-area').children().clone().appendTo(div);
      //   localStorage.setItem('drop-area', escape(div.html()));
      //   return null;
      // };
      this.init();
    },
    computed: {},
    data() {
      return {
        tools: [
          {
            index: 0,
            name: 'cat not-staging',
            bucket: 'cat-bucket bucket',
            value: 1,
            shape: cat_face,
            draggable: true,
            size: {x: 84, y: 84}
          },
          {
            index: 1,
            name: 'ten not-staging',
            bucket: 'ten-bucket bucket',
            value: 10,
            shape: ten_box,
            draggable: true,
            size: {x: 84, y: 84}
          },
          {
            index: 2,
            name: 'one-hundred not-staging',
            bucket: 'one-hundred-bucket bucket',
            value: 100,
            shape: one_hundred_box,
            draggable: true,
            size: {x: 84, y: 84}
          },
          {
            index: 3,
            name: 'one-thousand not-staging',
            bucket: 'one-thousand-bucket bucket',
            value: 1000,
            shape: one_thousand_box,
            draggable: true,
            size: {x: 84, y: 84}
          },
          {
            index: 4,
            name: 'ten-thousand not-staging',
            bucket: 'ten-thousand-bucket bucket',
            value: 10000,
            shape: ten_thousand_box,
            draggable: true,
            size: {x: 84, y: 84}
          },
          {
            index: 5,
            name: 'staging-tool-red staging-tool',
            bucket: 'staging-tool-red-bucket bucket',
            value: 'staging-tool',
            shape: red,
            draggable: true,
            size: {x: 78, y: 78}
          },
          {
            index: 6,
            name: 'staging-tool-yellow staging-tool',
            bucket: 'staging-tool-yellow-bucket bucket',
            value: 'staging-tool',
            shape: yellow,
            draggable: true,
            size: {x: 78, y: 78}
          },
          {
            index: 7,
            name: 'staging-tool-green staging-tool',
            bucket: 'staging-tool-green-bucket bucket',
            value: 'staging-tool',
            shape: green,
            draggable: true,
            size: {x: 78, y: 78}
          },
          {
            index: 8,
            name: 'trash',
            bucket: 'trash-bucket bucket',
            value: 'trash',
            shape: trashCan,
            draggable: false,
            size: {x: 84, y: 84}
          }
        ],
        droppedTools: []
      };
    },
    methods: {
      init() {
        const dropAreaDataFromLocalStorage = unescape(localStorage.getItem('drop-area'));
        // Reastablishes state if local storage is not empty.
        if (dropAreaDataFromLocalStorage !== null) {
          $(dropAreaDataFromLocalStorage).appendTo('.drop-area');
        }
        // Adds tools to sidebar
        _.each(this.tools, (tool, ind) => {
          this.newTool(tool);
        });
        this.droppable($(`.drop-area`));
        this.trashdroppable($(`.trash-bucket`));
        this.rightdroppable($(`.right-side-bar`));
        window.context = this;
        this.sizeWindow();
        window.addEventListener('resize', (evt) => {
          this.sizeWindow();
        });

      },
      sizeWindow() {
        this.windowX = window.innerWidth;
        this.windowY = window.innerHeight;
        $('#dom-counter').css({
          width: this.windowX,
          height: this.windowY
        });
      },
      // Generates and appends new tools to the sidebar
      newTool(tool) {
        // This conditional prevents errors when a tool is dropped on itself
        if (name !== 0) {
          const $bucket = $(`.${tool.bucket.split(' ')[0]}`);
          const $node = $.parseHTML(this.toolTemplate(tool.name, tool.value, tool.draggable, tool.shape, tool.index));

          $bucket.append($node);
          if (tool.name !== 'trash') {
            this.draggable($(`.${tool.name.split(' ')[0]}`), this);
          }
        }
      },
      // Template for tools
      toolTemplate(name, value, draggable, shape, index) {
        return `
          <div class="${name} counter tool _flex _single-flex _drag-cursor animated bounceInLeft" index="${index}" name="${name}" value="${value}" draggable="true">
            <img class="owned" src="${shape}">
          </div>
        `;
      },
      // Updates tools styling for positioning
      toolPosition(node, position, left, top) {
        node.css({
          position: position,
          left: left,
          top: top,
          width: '8vh',
          height: '8vh',
          'max-width': '5em',
          'max-height': '5em',
          'z-index': '1'

        });
      },
      // Changes size of staging tool with jquery animation
      // also removes tada animation and replaces it with pulse
      setStagingTool(node, value) {
        if (value === `staging-tool`) {
          node
            .removeClass(`tada`)
            .addClass(`pulse`)
            .css({
              position: `absolute`,
              height: `8em`,
              width: `30em`,
              'max-height': `10vh`,
              'max-width': `30vh`,
              'z-index': `1`
            });
          if (node.attr(`staged`) === `true`) {
            node.removeAttr(`staged`);
            node.children().each((ind, n) => {
              const $n = $(n);
              if (!$n.attr('class', 'owned')) {
                const position = $n.position();
                const left = position.left;
                const top = position.top;
                $n.css({
                  position: `absolute`,
                  left: left * 2,
                  top: top * 2,
                  width: '8vh',
                  height: '8vh',
                  'max-width': '5em',
                  'max-height': '5em',
                });
              }
            });
          }
          this.stagingdroppable(node);
        }
      },
      // Appends new tool to the sidebar if there is none there already
      addNewToolToSidebar(tool) {
        const $bucket = $(`.${tool.bucket.split(' ')[0]}`);
        if ($bucket.children().length === 0) {
          this.newTool(tool);
        }
      },
      // Sets staging tool in staging area
      setStagingToolInRightBar(node, value) {
        if (value === `staging-tool`) {
          node.attr(`stagingdroppable`, ``);
          node
            .animate({width: `15em`}, 400)
            .removeClass(`tada`)
            .addClass(`pulse`)
            .addClass(`_flex`)
            .attr(`staged`, `true`)
            .css({
              position: `relative`
            });
        }
      },
      draggable($element, context) {
        const ind = Number($element.attr('index'));
        let firstRun = true;
        $element.draggable({
          revert: `invalid`,
          containment: `window`,
          scroll: true,
          // cursorAt: {left: this.tools[ind].size.x/2, top: this.tools[ind].size.y/2},
          zIndex: '1000',
          appendTo: 'body',
          start: function (event, ui) {
            $(this).draggable('instance').offset.click = {
              left: Math.floor(ui.helper.width() / 2),
              top: Math.floor(ui.helper.height() / 2)
            };
          }
        });
      },
      // Function handles drop event for both jquery ui droppable and robot drop
      droppedInDropArea(event, ui) { // For robot drop just recreating objects with needed porperties
        const $node = $(ui.draggable);
        const $drop = $(event.target);
        const nodeName = $node.attr(`name`);
        const nodeValue = $node.attr(`value`);
        const nodeWidth = Number($node.css(`width`).split(`p`)[0]);
        const nodeHeight = Number($node.css(`height`).split(`p`)[0]);
        const left = event.clientX - (nodeWidth / 2);
        const top = event.clientY - (nodeHeight / 2);

        // Set nodes new position
        this.toolPosition($node, `absolute`, left, top);

        // Checks if node is a staging tool,
        // if true adds special conditions.
        this.setStagingTool($node, nodeValue);

        // // Appends the node to the DOM with angular directives compiled
        $node.removeClass('bounceInLeft');
        $node.addClass('dropped');
        $drop.append($node);
        $node.show();

        // // Appends new tool to sidbar
        // // This must come after the item has been appended or the length will not be correct

        this.addNewToolToSidebar(_.find(this.tools, (tool) => tool.name === nodeName));

        if (nodeName.split(' ')[1] === 'staging-tool') {
          $node.droppable('enable');
        }

        // // Increases count based on amount of kitties in the drop area
        // $timeout(ToolFactory.increaseCount.call(this, scope), 50);
      },
      droppable($element) {
        $element.droppable({
          accept: `.tool`,
          drop(event, ui) {
            const context = window.context;
            context.droppedInDropArea(event, ui);
          }
        });
      },
      trashdroppable($element) {
        $element.droppable({
          tolerance: "touch",
          hoverClass: `tada`,
          drop(event, ui) {
            const $node = $(ui.draggable);
            const nodeName = $node.attr(`name`);
            const nodeValue = $node.attr(`value`);

            const context = window.context;

            $node.removeClass(`tool`);
            $node.draggable(`destroy`);
            $node.remove();

            // $timeout(ToolFactory.increaseCount.call(this, scope), 50);

            // Appends new tool to sidbar
            // This must come after the item has been appended or the length will not be correct
            context.addNewToolToSidebar(_.find(context.tools, (tool) => tool.name === nodeName));
          }
        });
      },
      // function handles drop events on staging tools for both stagingdroppable and robot drop
      stagingDropped(event, ui) {
        const $node = $(ui.draggable);
        const $drop = $(event.target);
        const nodeName = $node.attr(`name`);
        const nodeValue = $node.attr(`value`);
        const nodeWidth = Number($node.css(`width`).split(`p`)[0]);
        const nodeHeight = Number($node.css(`height`).split(`p`)[0]);
        const nodeLeft = event.clientX;
        const nodeTop = event.clientY;
        const elLeft = $drop.position().left;
        const elTop = $drop.position().top;
        const left = (nodeLeft - elLeft) - (nodeWidth / 2);
        const top = (nodeTop - elTop) - (nodeHeight / 2);


        // Puts tool in in position inside staging tool.
        context.toolPosition($node, `absolute`, left, top);

        $node.css({
          width: '8vh',
          height: '8vh',
          'max-width': '5em',
          'max-height': '5em',
        });

        // // Appends the node to the DOM with angular directives compiled
        $node.removeClass('bounceInLeft');
        $drop.append($node);
        $node.show();

        // // Appends new tool to sidbar
        // // This must come after the item has been appended or the length will not be correct
        context.addNewToolToSidebar(_.find(context.tools, (tool) => tool.name === nodeName));

        /// Don't forget to make the drop area droppable again!
        $(`.drop-area`).droppable(`enable`);
      },
      stagingdroppable($element) {
        $element.droppable({
          accept: `.not-staging`,
          tolerance: `touch`,
          over() {
            $(`.drop-area`).droppable(`disable`);
          },
          out() {
            $(`.drop-area`).droppable(`enable`);
          },
          drop(event, ui) {
            const context = window.context;
            context.stagingDropped(event, ui);
          }
        });
      },
      rightdroppable($element) {
        $element.droppable({
          tolerance: "touch",
          // hoverClass: `right-side-bar-hover`,
          accept: `.staging-tool`,
          over() {
            $(`.drop-area`).droppable(`disable`);
          },
          out() {
            $(`.drop-area`).droppable(`enable`);
          },
          drop(event, ui) {
            const $node = $(ui.draggable);
            const $drop = $(event.target);
            const nodeName = $node.attr(`name`);
            const nodeValue = $node.attr(`value`);

            const context = window.context;

            // Set nodes new position
            context.toolPosition($node, ``, 0, 0);

            context.setStagingToolInRightBar($node, nodeValue);

            // // Appends the node to the DOM with angular directives compiled
            $node.removeClass('bounceInLeft');
            $drop.append($node);
            $node.removeClass('_single-flex');

            if ($node.attr('dropped') === 'true') {
              $node.droppable('disable');
            } else {
              $node.attr('dropped', 'true');
            }
            // This must come after the item has been appended or the length will not be correct
            context.addNewToolToSidebar(_.find(context.tools, (tool) => tool.name === nodeName));

            //Don't forget to make the drop area droppable again!
            $(`.drop-area`).droppable(`enable`);
          }
        });
      },
      goToProfile() {
        $('._flex').addClass('bounceOutUp');
        $('.profile').addClass('bounceOutUp');
        setTimeout(() => {
          this.$router.push({path: "profile"});
          $('._flex').removeClass('bounceOutUp');
          $('.profile').addClass('bounceOutUp');
        }, 1000);
      },
    },
    components: {
      draggable,
      Robot
    },
  };

</script>

<style scoped src="../css/counter.css"></style>
