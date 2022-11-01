let btn;
let win;

Plugin.register('bouncestyles', {
    title: 'BounceStyles',
    author: 'RyanV97',
    description: 'Exporting tool for making BounceStyles clothing items',
    icon: 'bar_chart',
    version: '0.1.0',
    variant: 'both',

    onload() {
        btn = new Action('export', {
           name: 'Export to BounceStyles',
           description: 'Helps export BounceStyles item data',
           icon: 'bar_chart',
           click: function() {
                win.show();
           }
        });
        MenuBar.addAction(btn, 'file');

        win = new Dialog('export_window', {
            title: 'BounceStyles Exporter',
            lines: [
                'If Model, Texture, or Animation is undefined, Name will be used by default in place of these. (Assuming model file ends in .geo.json, and animation ends in .animation.json)'
            ],
            form: {
                name: {label: 'Name', type: 'input'},
                model: {label: 'Model (Optional)', type: 'input', placeholder: 'namespace:path/to/model.json'},
                texture: {label: 'Texture (Optional)', type: 'input', placeholder: 'namespace:path/to/texture.png'},
                animation: {label: 'Animation (Optional)', type: 'input', placeholder: ''},
                //registry_name: {label: 'Registry Name', type: 'input'},
                head: {label: 'Head', type: 'checkbox'},
                body: {label: 'Body', type: 'checkbox'},
                legs: {label: 'Legs', type: 'checkbox'},
                feet: {label: 'Feet', type: 'checkbox'}
            },
            onConfirm: function(data) {
                Blockbench.export({
                    type: 'json',
                    content: `[
{
                            "name": ${data.name}
                        }
                    ]`
                });
            }
        });
    },

    onunload() {
        btn.delete();
    }

});