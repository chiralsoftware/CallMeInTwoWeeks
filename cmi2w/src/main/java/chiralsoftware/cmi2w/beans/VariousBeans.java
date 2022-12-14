package chiralsoftware.cmi2w.beans;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.logging.Logger;
import org.kefirsf.bb.BBProcessorFactory;
import org.kefirsf.bb.TextProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Create some useful beans
 */
@Configuration
public class VariousBeans {

    private static final Logger LOG = Logger.getLogger(VariousBeans.class.getName());

    private static final TextProcessor processor;

    // to make this work with Graal Native Image, we unfortunately need to use this hack
    // to load the config file because it's not finding it from resources.
    // if native-images fixes this we can remove this.
    private static final String defaultConfig
            = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4KPGNvbmZpZ3VyYXRpb24geG1s"
            // <editor-fold>
            + "bnM6eHNpPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxL1hNTFNjaGVtYS1pbnN0YW5jZSIKICAgICAg"
            + "ICAgICAgICAgeG1sbnM9Imh0dHA6Ly9rZWZpcnNmLm9yZy9rZWZpcmJiL3NjaGVtYSIKICAgICAg"
            + "ICAgICAgICAgeHNpOnNjaGVtYUxvY2F0aW9uPSJodHRwOi8va2VmaXJzZi5vcmcva2VmaXJiYi9z"
            + "Y2hlbWEgaHR0cDovL2tlZmlyc2Yub3JnL2tlZmlyYmIvc2NoZW1hL2tlZmlyYmItMS4yLnhzZCI+"
            + "CiAgICA8IS0tIFhNTCBlc2NhcGUgc3ltYm9scyAtLT4KICAgIDxzY29wZSBuYW1lPSJlc2NhcGVY"
            + "bWwiPgogICAgICAgIDxjb2RlIHByaW9yaXR5PSIxMDAiPgogICAgICAgICAgICA8cGF0dGVybj4m"
            + "YW1wOzwvcGF0dGVybj4KICAgICAgICAgICAgPHRlbXBsYXRlPiZhbXA7YW1wOzwvdGVtcGxhdGU+"
            + "CiAgICAgICAgPC9jb2RlPgogICAgICAgIDxjb2RlIHByaW9yaXR5PSIxMDAiPgogICAgICAgICAg"
            + "ICA8cGF0dGVybj4mYXBvczs8L3BhdHRlcm4+CiAgICAgICAgICAgIDx0ZW1wbGF0ZT4mYW1wO2Fw"
            + "b3M7PC90ZW1wbGF0ZT4KICAgICAgICA8L2NvZGU+CiAgICAgICAgPGNvZGUgcHJpb3JpdHk9IjEw"
            + "MCI+CiAgICAgICAgICAgIDxwYXR0ZXJuPiZsdDs8L3BhdHRlcm4+CiAgICAgICAgICAgIDx0ZW1w"
            + "bGF0ZT4mYW1wO2x0OzwvdGVtcGxhdGU+CiAgICAgICAgPC9jb2RlPgogICAgICAgIDxjb2RlIHBy"
            + "aW9yaXR5PSIxMDAiPgogICAgICAgICAgICA8cGF0dGVybj4mZ3Q7PC9wYXR0ZXJuPgogICAgICAg"
            + "ICAgICA8dGVtcGxhdGU+JmFtcDtndDs8L3RlbXBsYXRlPgogICAgICAgIDwvY29kZT4KICAgICAg"
            + "ICA8Y29kZSBwcmlvcml0eT0iMTAwIj4KICAgICAgICAgICAgPHBhdHRlcm4+JnF1b3Q7PC9wYXR0"
            + "ZXJuPgogICAgICAgICAgICA8dGVtcGxhdGU+JmFtcDtxdW90OzwvdGVtcGxhdGU+CiAgICAgICAg"
            + "PC9jb2RlPgogICAgPC9zY29wZT4KCiAgICA8IS0tIFNjb3BlIGZvciBlc2NhcGluZyBiYiBzcGVj"
            + "IGNoYXJzIC0tPgogICAgPHNjb3BlIG5hbWU9ImVzY2FwZUJiIiBwYXJlbnQ9ImVzY2FwZVhtbCI+"
            + "CiAgICAgICAgPCEtLQogICAgICAgICAgICBFc2NhcGUgYmItY29kZSBzeW1ib2xzCiAgICAgICAg"
            + "ICAgIGRvdWJsZSBzbGFzaCB0byBzbGFzaAogICAgICAgICAgICBzbGFzaCArIHNxdWFyZSBicmFj"
            + "a2V0IHRvIHNxdWFyZSBicmFja2V0CiAgICAgICAgIC0tPgogICAgICAgIDxjb2RlIG5hbWU9InNs"
            + "YXNoIiBwcmlvcml0eT0iMTAiPgogICAgICAgICAgICA8cGF0dGVybj5cXDwvcGF0dGVybj4KICAg"
            + "ICAgICAgICAgPHRlbXBsYXRlPlw8L3RlbXBsYXRlPgogICAgICAgIDwvY29kZT4KICAgICAgICA8"
            + "Y29kZSBuYW1lPSJsZWZ0X3NxdWFyZV9icmFja2V0IiBwcmlvcml0eT0iOSI+CiAgICAgICAgICAg"
            + "IDxwYXR0ZXJuPlxbPC9wYXR0ZXJuPgogICAgICAgICAgICA8dGVtcGxhdGU+WzwvdGVtcGxhdGU+"
            + "CiAgICAgICAgPC9jb2RlPgogICAgICAgIDxjb2RlIG5hbWU9InJpZ2h0X3NxdWFyZV9icmFja2V0"
            + "IiBwcmlvcml0eT0iOSI+CiAgICAgICAgICAgIDxwYXR0ZXJuPlxdPC9wYXR0ZXJuPgogICAgICAg"
            + "ICAgICA8dGVtcGxhdGU+XTwvdGVtcGxhdGU+CiAgICAgICAgPC9jb2RlPgogICAgICAgIDxjb2Rl"
            + "cmVmIG5hbWU9ImNvbW1lbnQiLz4KICAgIDwvc2NvcGU+CgogICAgPCEtLSBDb21tZW50IC0tPgog"
            + "ICAgPGNvZGUgbmFtZT0iY29tbWVudCI+CiAgICAgICAgPHBhdHRlcm4+Wyo8anVuay8+Kl08L3Bh"
            + "dHRlcm4+CiAgICAgICAgPHRlbXBsYXRlLz4KICAgIDwvY29kZT4KCiAgICA8IS0tIEVzY2FwZSBi"
            + "YXNpYyBIVE1MIGNoYXIgc2VxdWVuY2VzIC0tPgogICAgPHNjb3BlIG5hbWU9ImJhc2ljIiBwYXJl"
            + "bnQ9ImVzY2FwZUJiIj4KICAgICAgICA8IS0tIGxpbmUgZmVlZCBjaGFyYWN0ZXJzIC0tPgogICAg"
            + "ICAgIDxjb2RlIG5hbWU9ImJyIj4KICAgICAgICAgICAgPHBhdHRlcm4+PGVvbC8+PC9wYXR0ZXJu"
            + "PgogICAgICAgICAgICA8dGVtcGxhdGU+Jmx0O2JyLyZndDs8L3RlbXBsYXRlPgogICAgICAgIDwv"
            + "Y29kZT4KCiAgICAgICAgPCEtLSBTcGVjaWFsIGh0bWwgc3ltYm9scyAtLT4KICAgICAgICA8Y29k"
            + "ZSBuYW1lPSJzeW1ib2wiPgogICAgICAgICAgICA8cGF0dGVybiBpZ25vcmVDYXNlPSJ0cnVlIj5b"
            + "c3ltYm9sPTx2YXIgc2NvcGU9ImVzY2FwZVhtbCIvPi9dPC9wYXR0ZXJuPgogICAgICAgICAgICA8"
            + "dGVtcGxhdGU+JmFtcDs8dmFyLz47PC90ZW1wbGF0ZT4KICAgICAgICA8L2NvZGU+CgogICAgICAg"
            + "IDwhLS0gYW5nbGUgcXVvdGVzIC0tPgogICAgICAgIDxjb2RlIG5hbWU9ImFxdW90ZSI+CiAgICAg"
            + "ICAgICAgIDxwYXR0ZXJuIGlnbm9yZUNhc2U9InRydWUiPlthcXVvdGVdPHZhciBpbmhlcml0PSJ0"
            + "cnVlIi8+Wy9hcXVvdGVdPC9wYXR0ZXJuPgogICAgICAgICAgICA8dGVtcGxhdGU+JmFtcDtsYXF1"
            + "bzs8dmFyLz4mYW1wO3JhcXVvOzwvdGVtcGxhdGU+CiAgICAgICAgPC9jb2RlPgogICAgPC9zY29w"
            + "ZT4KCiAgICA8IS0tIFJvb3Qgc2NvcGUuIFRoaXMgc2NvcGUgdXNlcyB3aGVuIHByb2Nlc3NvciBz"
            + "dGFydGVkIHdvcmsgYW5kIGJ5IGRlZmF1bHQsIGlmIG5vdCBzZXQgb3RoZXIgc2NvcGUgLS0+CiAg"
            + "ICA8c2NvcGUgbmFtZT0iUk9PVCIgcGFyZW50PSJiYXNpYyI+CiAgICAgICAgPCEtLSBGb3JtYXR0"
            + "aW5nIC0tPgogICAgICAgIDxjb2RlcmVmIG5hbWU9ImJvbGQiLz4KICAgICAgICA8Y29kZXJlZiBu"
            + "YW1lPSJ1Ii8+CiAgICAgICAgPGNvZGVyZWYgbmFtZT0icyIvPgogICAgICAgIDxjb2RlcmVmIG5h"
            + "bWU9ImkiLz4KICAgICAgICA8Y29kZXJlZiBuYW1lPSJjb2xvciIvPgogICAgICAgIDxjb2RlcmVm"
            + "IG5hbWU9InNpemUiLz4KCiAgICAgICAgPCEtLSBRdW90ZXMgLS0+CiAgICAgICAgPGNvZGVyZWYg"
            + "bmFtZT0iY29kZSIvPgogICAgICAgIDxjb2RlcmVmIG5hbWU9InF1b3RlIi8+CgogICAgICAgIDwh"
            + "LS0gSW1hZ2VzIC0tPgogICAgICAgIDxjb2RlcmVmIG5hbWU9ImltZzEiLz4KICAgICAgICA8Y29k"
            + "ZXJlZiBuYW1lPSJpbWcyIi8+CgogICAgICAgIDwhLS0gbGlua3MgLS0+CiAgICAgICAgPGNvZGVy"
            + "ZWYgbmFtZT0idXJsMSIvPgogICAgICAgIDxjb2RlcmVmIG5hbWU9InVybDIiLz4KICAgICAgICA8"
            + "Y29kZXJlZiBuYW1lPSJ1cmwzIi8+CiAgICAgICAgPGNvZGVyZWYgbmFtZT0idXJsNCIvPgoKICAg"
            + "ICAgICA8IS0tIFRhYmxlIC0tPgogICAgICAgIDxjb2RlcmVmIG5hbWU9InRhYmxlIi8+CgogICAg"
            + "ICAgIDwhLS0gTGlzdCAtLT4KICAgICAgICA8Y29kZXJlZiBuYW1lPSJsaXN0Ii8+CiAgICA8L3Nj"
            + "b3BlPgoKICAgIDwhLS0gU2ltcGxlIGZvcm1hdHRpbmcgLS0+CiAgICA8Y29kZSBuYW1lPSJib2xk"
            + "Ij4KICAgICAgICA8cGF0dGVybiBpZ25vcmVDYXNlPSJ0cnVlIj5bYl08dmFyIGluaGVyaXQ9InRy"
            + "dWUiLz5bL2JdPC9wYXR0ZXJuPgogICAgICAgIDx0ZW1wbGF0ZT4mbHQ7YiZndDs8dmFyLz4mbHQ7"
            + "L2ImZ3Q7PC90ZW1wbGF0ZT4KICAgIDwvY29kZT4KICAgIDxjb2RlIG5hbWU9InUiPgogICAgICAg"
            + "IDxwYXR0ZXJuIGlnbm9yZUNhc2U9InRydWUiPlt1XTx2YXIgaW5oZXJpdD0idHJ1ZSIvPlsvdV08"
            + "L3BhdHRlcm4+CiAgICAgICAgPHRlbXBsYXRlPiZsdDt1Jmd0Ozx2YXIvPiZsdDsvdSZndDs8L3Rl"
            + "bXBsYXRlPgogICAgPC9jb2RlPgogICAgPGNvZGUgbmFtZT0icyI+CiAgICAgICAgPHBhdHRlcm4g"
            + "aWdub3JlQ2FzZT0idHJ1ZSI+W3NdPHZhciBpbmhlcml0PSJ0cnVlIi8+Wy9zXTwvcGF0dGVybj4K"
            + "ICAgICAgICA8dGVtcGxhdGU+Jmx0O3MmZ3Q7PHZhci8+Jmx0Oy9zJmd0OzwvdGVtcGxhdGU+CiAg"
            + "ICA8L2NvZGU+CiAgICA8Y29kZSBuYW1lPSJpIj4KICAgICAgICA8cGF0dGVybiBpZ25vcmVDYXNl"
            + "PSJ0cnVlIj5baV08dmFyIGluaGVyaXQ9InRydWUiLz5bL2ldPC9wYXR0ZXJuPgogICAgICAgIDx0"
            + "ZW1wbGF0ZT4mbHQ7aSZndDs8dmFyLz4mbHQ7L2kmZ3Q7PC90ZW1wbGF0ZT4KICAgIDwvY29kZT4K"
            + "CiAgICA8IS0tIEZvbnQgY29sb3IgLS0+CiAgICA8Y29kZSBuYW1lPSJjb2xvciI+CiAgICAgICAg"
            + "PHBhdHRlcm4gaWdub3JlQ2FzZT0idHJ1ZSI+W2NvbG9yPTx2YXIgbmFtZT0iY29sb3IiIHNjb3Bl"
            + "PSJlc2NhcGVYbWwiLz5dPHZhciBuYW1lPSJ0ZXh0IiBpbmhlcml0PSJ0cnVlIi8+Wy9jb2xvcl08"
            + "L3BhdHRlcm4+CiAgICAgICAgPHRlbXBsYXRlPiZsdDtzcGFuIHN0eWxlPSZxdW90O2NvbG9yOjx2"
            + "YXIgbmFtZT0iY29sb3IiLz47JnF1b3Q7Jmd0Ozx2YXIgbmFtZT0idGV4dCIvPiZsdDsvc3BhbiZn"
            + "dDs8L3RlbXBsYXRlPgogICAgPC9jb2RlPgoKICAgIDwhLS0gRm9udCBzaXplIC0tPgogICAgPGNv"
            + "ZGUgbmFtZT0ic2l6ZSI+CiAgICAgICAgPHBhdHRlcm4gaWdub3JlQ2FzZT0idHJ1ZSI+W3NpemU9"
            + "PHZhciBuYW1lPSJzaXplIiBzY29wZT0iZXNjYXBlWG1sIi8+XTx2YXIgbmFtZT0idGV4dCIgaW5o"
            + "ZXJpdD0idHJ1ZSIvPlsvc2l6ZV08L3BhdHRlcm4+CiAgICAgICAgPHRlbXBsYXRlPiZsdDtzcGFu"
            + "IHN0eWxlPSZxdW90O2ZvbnQtc2l6ZTo8dmFyIG5hbWU9InNpemUiLz47JnF1b3Q7Jmd0Ozx2YXIg"
            + "bmFtZT0idGV4dCIvPiZsdDsvc3BhbiZndDs8L3RlbXBsYXRlPgogICAgPC9jb2RlPgoKICAgIDwh"
            + "LS0gSW5zZXJ0IGltYWdlIC0tPgogICAgPGNvZGUgbmFtZT0iaW1nMSIgcHJpb3JpdHk9IjMiPgog"
            + "ICAgICAgIDxwYXR0ZXJuIGlnbm9yZUNhc2U9InRydWUiPltpbWddPHVybCBsb2NhbD0idHJ1ZSIv"
            + "PlsvaW1nXTwvcGF0dGVybj4KICAgICAgICA8dGVtcGxhdGU+Jmx0O2ltZyBzcmM9JnF1b3Q7PHZh"
            + "ciBuYW1lPSJ1cmwiLz4mcXVvdDsvJmd0OzwvdGVtcGxhdGU+CiAgICA8L2NvZGU+CiAgICA8Y29k"
            + "ZSBuYW1lPSJpbWcyIiBwcmlvcml0eT0iMSI+CiAgICAgICAgPHBhdHRlcm4gaWdub3JlQ2FzZT0i"
            + "dHJ1ZSI+W2ltZ108dXJsIHNjaGVtYWxlc3M9InRydWUiLz5bL2ltZ108L3BhdHRlcm4+CiAgICAg"
            + "ICAgPHRlbXBsYXRlPiZsdDtpbWcgc3JjPSZxdW90O2h0dHA6Ly88dmFyIG5hbWU9InVybCIvPiZx"
            + "dW90Oy8mZ3Q7PC90ZW1wbGF0ZT4KICAgIDwvY29kZT4KCiAgICA8IS0tIExpbmtzLiBodHRwLCBo"
            + "dHRwcywgbWFpbHRvIHByb3RvY29scyAtLT4KICAgIDxzY29wZSBuYW1lPSJ1cmwiIHBhcmVudD0i"
            + "YmFzaWMiPgogICAgICAgIDxjb2RlcmVmIG5hbWU9ImJvbGQiLz4KICAgICAgICA8Y29kZXJlZiBu"
            + "YW1lPSJ1Ii8+CiAgICAgICAgPGNvZGVyZWYgbmFtZT0icyIvPgogICAgICAgIDxjb2RlcmVmIG5h"
            + "bWU9ImkiLz4KICAgICAgICA8Y29kZXJlZiBuYW1lPSJjb2xvciIvPgogICAgICAgIDxjb2RlcmVm"
            + "IG5hbWU9InNpemUiLz4KCiAgICAgICAgPGNvZGVyZWYgbmFtZT0iaW1nMSIvPgogICAgICAgIDxj"
            + "b2RlcmVmIG5hbWU9ImltZzIiLz4KICAgIDwvc2NvcGU+CgogICAgPCEtLSBIVFRQIC0tPgogICAg"
            + "PGNvZGUgbmFtZT0idXJsMSIgcHJpb3JpdHk9IjMiPgogICAgICAgIDxwYXR0ZXJuIGlnbm9yZUNh"
            + "c2U9InRydWUiPlt1cmw9PHVybCBsb2NhbD0idHJ1ZSIvPl08dmFyIG5hbWU9InRleHQiIHNjb3Bl"
            + "PSJ1cmwiLz5bL3VybF08L3BhdHRlcm4+CiAgICAgICAgPHRlbXBsYXRlPiZsdDthIGhyZWY9JnF1"
            + "b3Q7PHZhciBuYW1lPSJ1cmwiLz4mcXVvdDsmZ3Q7PHZhciBuYW1lPSJ0ZXh0Ii8+Jmx0Oy9hJmd0"
            + "OzwvdGVtcGxhdGU+CiAgICA8L2NvZGU+CiAgICA8Y29kZSBuYW1lPSJ1cmwyIiBwcmlvcml0eT0i"
            + "MyI+CiAgICAgICAgPHBhdHRlcm4gaWdub3JlQ2FzZT0idHJ1ZSI+W3VybF08dXJsIGxvY2FsPSJ0"
            + "cnVlIi8+Wy91cmxdPC9wYXR0ZXJuPgogICAgICAgIDx0ZW1wbGF0ZT4mbHQ7YSBocmVmPSZxdW90"
            + "Ozx2YXIgbmFtZT0idXJsIi8+JnF1b3Q7Jmd0Ozx2YXIgbmFtZT0idXJsIi8+Jmx0Oy9hJmd0Ozwv"
            + "dGVtcGxhdGU+CiAgICA8L2NvZGU+CiAgICA8Y29kZSBuYW1lPSJ1cmwzIiBwcmlvcml0eT0iMSI+"
            + "CiAgICAgICAgPHBhdHRlcm4gaWdub3JlQ2FzZT0idHJ1ZSI+W3VybD08dXJsIHNjaGVtYWxlc3M9"
            + "InRydWUiLz5dPHZhciBuYW1lPSJ0ZXh0IiBzY29wZT0idXJsIi8+Wy91cmxdPC9wYXR0ZXJuPgog"
            + "ICAgICAgIDx0ZW1wbGF0ZT4mbHQ7YSBocmVmPSZxdW90O2h0dHA6Ly88dmFyIG5hbWU9InVybCIv"
            + "PiZxdW90OyZndDs8dmFyIG5hbWU9InRleHQiLz4mbHQ7L2EmZ3Q7PC90ZW1wbGF0ZT4KICAgIDwv"
            + "Y29kZT4KICAgIDxjb2RlIG5hbWU9InVybDQiIHByaW9yaXR5PSIxIj4KICAgICAgICA8cGF0dGVy"
            + "biBpZ25vcmVDYXNlPSJ0cnVlIj5bdXJsXTx1cmwgc2NoZW1hbGVzcz0idHJ1ZSIvPlsvdXJsXTwv"
            + "cGF0dGVybj4KICAgICAgICA8dGVtcGxhdGU+Jmx0O2EgaHJlZj0mcXVvdDtodHRwOi8vPHZhciBu"
            + "YW1lPSJ1cmwiLz4mcXVvdDsmZ3Q7PHZhciBuYW1lPSJ1cmwiLz4mbHQ7L2EmZ3Q7PC90ZW1wbGF0"
            + "ZT4KICAgIDwvY29kZT4KCiAgICA8IS0tIFF1b3RlIGJsb2NrIC0tPgogICAgPGNvZGUgbmFtZT0i"
            + "cXVvdGUiPgogICAgICAgIDxwYXR0ZXJuIGlnbm9yZUNhc2U9InRydWUiPltxdW90ZV08dmFyIGlu"
            + "aGVyaXQ9InRydWUiLz5bL3F1b3RlXTwvcGF0dGVybj4KICAgICAgICA8dGVtcGxhdGU+Jmx0O2Js"
            + "b2NrcXVvdGUmZ3Q7PHZhci8+Jmx0Oy9ibG9ja3F1b3RlJmd0OzwvdGVtcGxhdGU+CiAgICA8L2Nv"
            + "ZGU+CgogICAgPCEtLSBRdW90ZSBjb2RlIGJsb2NrIC0tPgogICAgPGNvZGUgbmFtZT0iY29kZSI+"
            + "CiAgICAgICAgPHBhdHRlcm4gaWdub3JlQ2FzZT0idHJ1ZSI+W2NvZGVdPHZhciBzY29wZT0iYmFz"
            + "aWMiLz5bL2NvZGVdPC9wYXR0ZXJuPgogICAgICAgIDx0ZW1wbGF0ZT4mbHQ7cHJlJmd0Ozx2YXIv"
            + "PiZsdDsvcHJlJmd0OzwvdGVtcGxhdGU+CiAgICA8L2NvZGU+CgogICAgPCEtLSBTaW1wbGUgdGFi"
            + "bGUgLS0+CiAgICA8Y29kZSBuYW1lPSJ0YWJsZSI+CiAgICAgICAgPHBhdHRlcm4gaWdub3JlQ2Fz"
            + "ZT0idHJ1ZSI+W3RhYmxlXTx2YXIgc2NvcGU9InRhYmxlU2NvcGUiLz5bL3RhYmxlXTwvcGF0dGVy"
            + "bj4KICAgICAgICA8dGVtcGxhdGU+Jmx0O3RhYmxlJmd0Ozx2YXIvPiZsdDsvdGFibGUmZ3Q7PC90"
            + "ZW1wbGF0ZT4KICAgIDwvY29kZT4KICAgIDxzY29wZSBuYW1lPSJ0YWJsZVNjb3BlIiBpZ25vcmVU"
            + "ZXh0PSJ0cnVlIj4KICAgICAgICA8Y29kZSBuYW1lPSJ0ciI+CiAgICAgICAgICAgIDxwYXR0ZXJu"
            + "IGlnbm9yZUNhc2U9InRydWUiPlt0cl08dmFyIHNjb3BlPSJ0clNjb3BlIi8+Wy90cl08L3BhdHRl"
            + "cm4+CiAgICAgICAgICAgIDx0ZW1wbGF0ZT4mbHQ7dHImZ3Q7PHZhci8+Jmx0Oy90ciZndDs8L3Rl"
            + "bXBsYXRlPgogICAgICAgIDwvY29kZT4KICAgICAgICA8Y29kZXJlZiBuYW1lPSJjb21tZW50Ii8+"
            + "CiAgICA8L3Njb3BlPgogICAgPHNjb3BlIG5hbWU9InRyU2NvcGUiIGlnbm9yZVRleHQ9InRydWUi"
            + "PgogICAgICAgIDxjb2RlIG5hbWU9InRoIj4KICAgICAgICAgICAgPHBhdHRlcm4gaWdub3JlQ2Fz"
            + "ZT0idHJ1ZSI+W3RoXTx2YXIvPlsvdGhdPC9wYXR0ZXJuPgogICAgICAgICAgICA8dGVtcGxhdGU+"
            + "Jmx0O3RoJmd0Ozx2YXIvPiZsdDsvdGgmZ3Q7PC90ZW1wbGF0ZT4KICAgICAgICA8L2NvZGU+CiAg"
            + "ICAgICAgPGNvZGUgbmFtZT0idGQiPgogICAgICAgICAgICA8cGF0dGVybiBpZ25vcmVDYXNlPSJ0"
            + "cnVlIj5bdGRdPHZhci8+Wy90ZF08L3BhdHRlcm4+CiAgICAgICAgICAgIDx0ZW1wbGF0ZT4mbHQ7"
            + "dGQmZ3Q7PHZhci8+Jmx0Oy90ZCZndDs8L3RlbXBsYXRlPgogICAgICAgIDwvY29kZT4KICAgICAg"
            + "ICA8Y29kZXJlZiBuYW1lPSJjb21tZW50Ii8+CiAgICA8L3Njb3BlPgoKICAgIDwhLS0gU3RhbmRh"
            + "cmQgQkIgbGlzdCAtLT4KICAgIDxjb2RlIG5hbWU9Imxpc3QiIHByaW9yaXR5PSIyIj4KICAgICAg"
            + "ICA8cGF0dGVybiBpZ25vcmVDYXNlPSJ0cnVlIj5bbGlzdF08dmFyIG5hbWU9ImNvbnRlbnQiIHNj"
            + "b3BlPSJsaXN0U2NvcGUiIC8+Wy9saXN0XTwvcGF0dGVybj4KICAgICAgICA8dGVtcGxhdGU+Jmx0"
            + "O3VsJmd0Ozx2YXIgbmFtZT0iY29udGVudCIvPiZsdDsvdWwmZ3Q7PC90ZW1wbGF0ZT4KICAgIDwv"
            + "Y29kZT4KICAgIDxzY29wZSBuYW1lPSJsaXN0U2NvcGUiIGlnbm9yZVRleHQ9InRydWUiPgogICAg"
            + "ICAgIDxjb2RlPgogICAgICAgICAgICA8cGF0dGVybj5bKl08dmFyIHNjb3BlPSJST09UIi8+PGNv"
            + "bnN0YW50IHZhbHVlPSJbKl0iIGdob3N0PSJ0cnVlIi8+PC9wYXR0ZXJuPgogICAgICAgICAgICA8"
            + "cGF0dGVybj4qPHZhciBzY29wZT0iUk9PVCIvPjxjb25zdGFudCB2YWx1ZT0iKiIgZ2hvc3Q9InRy"
            + "dWUiLz48L3BhdHRlcm4+CiAgICAgICAgICAgIDxwYXR0ZXJuPlsqXTx2YXIgc2NvcGU9IlJPT1Qi"
            + "Lz48Y29uc3RhbnQgdmFsdWU9IioiIGdob3N0PSJ0cnVlIi8+PC9wYXR0ZXJuPgogICAgICAgICAg"
            + "ICA8cGF0dGVybj4qPHZhciBzY29wZT0iUk9PVCIvPjxjb25zdGFudCB2YWx1ZT0iWypdIiBnaG9z"
            + "dD0idHJ1ZSIvPjwvcGF0dGVybj4KICAgICAgICAgICAgPHBhdHRlcm4+WypdPHZhciBzY29wZT0i"
            + "Uk9PVCIvPjwvcGF0dGVybj4KICAgICAgICAgICAgPHBhdHRlcm4+Kjx2YXIgc2NvcGU9IlJPT1Qi"
            + "Lz48L3BhdHRlcm4+CiAgICAgICAgICAgIDx0ZW1wbGF0ZT4mbHQ7bGkmZ3Q7PHZhci8+Jmx0Oy9s"
            + "aSZndDs8L3RlbXBsYXRlPgogICAgICAgIDwvY29kZT4KICAgIDwvc2NvcGU+CjwvY29uZmlndXJh"
            + "dGlvbj4=";
    // </editor-fold>

    static {
        processor = BBProcessorFactory.getInstance().create(new ByteArrayInputStream(Base64.getDecoder().decode(defaultConfig)));
        LOG.info("TextProcessor created");
//         processor = BBProcessorFactory.getInstance().create();
    }

    @Bean(name = "bbCode")
    public TextProcessor textProcessor() {
        return processor;
    }

}
