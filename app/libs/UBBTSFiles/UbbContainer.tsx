// A '.tsx' file enables JSX support in the TypeScript compiler, 
// for more information see the following page on the TypeScript wiki:
// https://github.com/Microsoft/TypeScript/wiki/JSX

import * as React from 'react';
import * as ReactDOM from 'react-dom';
import * as Ubb from '../Ubb/UbbCodeExtension';
declare let moment: any;

export function generateWebView(Container, postsInfo, usersInfo,topicInfo,boardInfo) {
    let postsUser = [];
    for (let i in postsInfo) {
        //找到用户 
        for (let j in usersInfo) {
            if (usersInfo[j].name === postsInfo[i].userName) {
                postsUser[i] = usersInfo[j];
            }
        }     
    }
    let data = [];
    if (!topicInfo.isAnonymous)
    for (let i in postsInfo) {
        data[i] = { postInfo: postsInfo[i], userInfo: postsUser[i]}
        }
    else
        for (let i in postsInfo) {
            data[i] = { postInfo: postsInfo[i], userInfo: usersInfo[i] }
        }
    let topicUI =
        <div style={{ width: "100%", display: "flex", flexDirection: "column" }}>
            <div style={{ width: "100%", display: "flex", flexDirection: "column", backgroundColor: "#8dc9db", marginBottom: "1rem", color: "#fff" }}>
                <div style={{ width: "100%", fontSize: "1.5rem", marginLeft: "0.5rem", marginRight: "0.5rem" }}>{topicInfo.title}</div>
                <div style={{ width: "100%", display: "flex", marginLeft:"0.5rem" }}>
                    {boardInfo.name}
                </div>
                <div style={{ width: "100%", display: "flex", alignItems: "center", marginLeft:"0.5rem" }}>
                    <div>点击:{topicInfo.hitCount} </div>  <div style={{ marginLeft: "1rem" }}>  {moment(topicInfo.time).format('YYYY-MM-DD HH:mm')} </div> 
                </div>
            </div>
            {data.map(generatePost)}
        </div>;
    ReactDOM.render(topicUI,Container);
}
function generatePost(data) {
    let postUI =
        <div style={{ width: "100%", display: "flex", flexDirection: "column", marginBottom:"1rem" }}>
            <div style={{ width: "100%", display: "flex", flexDirection: "column", backgroundColor: "#8dc9db", color: "#fff" }}>
                <div style={{ width: "100%", display: "flex", alignItems: "center", marginTop: "0.5rem", marginBottom:"0.5rem" }}>
                <img style={{ width: "2rem", marginLeft:"1rem", height: "2rem", borderRadius: "50%" }} src={data.userInfo.portraitUrl} />
                <div style={{ marginLeft: "1rem" }}>{data.userInfo.name}</div>
                </div>
                <div style={{ width: "100%", display: "flex", alignItems: "center", marginBottom:"0.5rem" }}>
                    <div style={{ marginLeft: "1rem" }}>{data.postInfo.floor}楼</div>
                    <div style={{ marginLeft: "1rem" }}>{moment(data.postInfo.time).format('YYYY-MM-DD HH:mm')}</div>
                </div>
            </div>
            <UbbContainer code={data.postInfo.content} />
            <div style={{ borderTop: "#eaeaea solid thin", borderBottom:"#eaeaea solid thin" }}><UbbContainer code={data.userInfo.signatureCode} /></div>
        </div>;
    return postUI;
}
/**
 * 定义 UBBContainer 组件需要使用的属性。
 */
export class UbbContainerProps {
	/**
	 * UBB 容器需要解析的 UBB 代码字符串。
	 */
	code: string;
	/**
	 * UBB 容器使用的 UBB 引擎。引擎可在多次解析时重复使用，从而实现对整个文档的跨容器状态维护（如对特定内容进行统一计数等）。
	 */
	engine?: Ubb.UbbCodeEngine;
	/**
	 * UBB 解析时使用的相关选项。
	 */
	options?: Ubb.UbbCodeOptions;
}

/**
 * 提供用于解析 UBB 的核心组件。
 */
export class UbbContainer extends React.Component<UbbContainerProps, {}> {

	render() {

		// 获取引擎对象，如果不使用引擎对象则创建一个默认的
		const engine = this.props.engine || Ubb.createEngine();
		// 获取选项，如果不设置选项则创建一个默认的
		const options = this.props.options || new Ubb.UbbCodeOptions();
        //code为一段UBB字符串
        let { code } = this.props;

        //替换掉code中形如&#\d{5};的部分
        //let r = /&#(\d{5});/gi;
        //code = code.replace(r, (match, grp) => (String.fromCharCode(grp)));

        const ubbHtml = engine.exec(code || '', options);
        
		//打开回车与空格
		const style = {
			whiteSpace: 'pre-wrap',
            width: "100%"
		};

		// 注意兼容性设置， HTML4 不支持 article 标签
		if (options.compatibility === Ubb.UbbCompatiblityMode.Transitional) {
			return <blockquote style={style}>{ubbHtml}</blockquote>;
		} else {
			return <article style={style}>{ubbHtml}</article>;
		}
	}

}